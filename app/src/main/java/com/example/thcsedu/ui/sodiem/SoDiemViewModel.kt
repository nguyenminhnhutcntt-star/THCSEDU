package com.example.thcsedu.ui.sodiem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.entity.BangDiemEntity
import com.example.thcsedu.data.local.entity.LichSuSuaDiemEntity
import com.example.thcsedu.domain.repository.BangDiemRepository
import com.example.thcsedu.domain.repository.LichSuSuaDiemRepository
import com.example.thcsedu.domain.usecase.TinhDiemVaXepLoaiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoDiemViewModel @Inject constructor(
    private val repository: BangDiemRepository,
    private val lichSuSuaDiemRepository: LichSuSuaDiemRepository,
    private val tinhDiemUseCase: TinhDiemVaXepLoaiUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SoDiemUiState())
    val uiState: StateFlow<SoDiemUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun toggleGradeLock() {
        _uiState.update { it.copy(isGradeLocked = !it.isGradeLocked) }
    }

    fun dismissPendingGradeChange() {
        _uiState.update { it.copy(pendingGradeChange = null) }
    }

    fun loadSoDiem(
        maLop: String,
        maMonHoc: String,
        hocKy: Int,
        namHoc: String = "2024-2025"
    ) {
        loadJob?.cancel()
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                currentMaMonHoc = maMonHoc,
                currentHocKy = hocKy,
                currentNamHoc = namHoc
            )
        }

        loadJob = viewModelScope.launch {
            combine(
                repository.getHocSinhByLop(maLop),
                repository.getBangDiem(maLop, maMonHoc, hocKy)
            ) { hocSinhList, bangDiemList ->
                val bangDiemMap = bangDiemList
                    .filter { it.namHoc == namHoc }
                    .associateBy { it.maHocSinh }

                hocSinhList.map { hocSinh ->
                    val bangDiem = bangDiemMap[hocSinh.maHocSinh]
                    val dsDiemTX = bangDiem?.dsDiemTX ?: emptyList()
                    val diemGK = bangDiem?.diemGiuaKy
                    val diemCK = bangDiem?.diemCuoiKy
                    val nhanXet = bangDiem?.nhanXet

                    val diemTB = tinhDiemUseCase.tinhDiemTBMonHK(
                        dsDiemTX = dsDiemTX,
                        diemGK = diemGK,
                        diemCK = diemCK
                    )

                    HocSinhDiemItemUiState(
                        maHocSinh = hocSinh.maHocSinh,
                        hoTen = hocSinh.hoTen,
                        dsDiemTX = dsDiemTX,
                        diemGiuaKy = diemGK,
                        diemCuoiKy = diemCK,
                        diemTBMon = diemTB,
                        nhanXet = nhanXet
                    )
                }
            }.catch { throwable ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = throwable.localizedMessage ?: "Unknown error"
                    )
                }
            }.collect { dsHocSinhDiem ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        dsHocSinhDiem = dsHocSinhDiem,
                        errorMessage = null
                    )
                }
            }
        }
    }

    fun updateDiemTX(maHocSinh: String, index: Int, value: Float) {
        if (_uiState.value.isGradeLocked) return

        val studentItem = _uiState.value.dsHocSinhDiem.find { it.maHocSinh == maHocSinh }
        val oldVal = if (studentItem != null && index in studentItem.dsDiemTX.indices) {
            studentItem.dsDiemTX[index]
        } else {
            null
        }

        val applyAction = {
            _uiState.update { currentState ->
                val updatedList = currentState.dsHocSinhDiem.map { item ->
                    if (item.maHocSinh == maHocSinh) {
                        val newDiemTX = item.dsDiemTX.toMutableList().apply {
                            if (index in indices) {
                                this[index] = value
                            } else if (index == size) {
                                add(value)
                            }
                        }
                        val newDiemTB = tinhDiemUseCase.tinhDiemTBMonHK(
                            dsDiemTX = newDiemTX,
                            diemGK = item.diemGiuaKy,
                            diemCK = item.diemCuoiKy
                        )
                        item.copy(
                            dsDiemTX = newDiemTX,
                            diemTBMon = newDiemTB
                        )
                    } else {
                        item
                    }
                }
                currentState.copy(dsHocSinhDiem = updatedList, pendingGradeChange = null)
            }
        }

        if (oldVal != null) {
            _uiState.update {
                it.copy(
                    pendingGradeChange = PendingGradeChange(
                        maHocSinh = maHocSinh,
                        loaiDiem = "ĐĐGtx (${index + 1})",
                        diemCu = oldVal,
                        diemMoi = value,
                        onConfirm = { reason ->
                            viewModelScope.launch {
                                lichSuSuaDiemRepository.logGradeChange(
                                    LichSuSuaDiemEntity(
                                        maHocSinh = maHocSinh,
                                        maMonHoc = _uiState.value.currentMaMonHoc,
                                        loaiDiem = "ĐĐGtx (${index + 1})",
                                        diemCu = oldVal,
                                        diemMoi = value,
                                        lyDoSua = reason,
                                        nguoiSua = "Giáo viên bộ môn"
                                    )
                                )
                                applyAction()
                            }
                        }
                    )
                )
            }
        } else {
            applyAction()
        }
    }

    fun updateDiemGK(maHocSinh: String, value: Float) {
        if (_uiState.value.isGradeLocked) return

        val studentItem = _uiState.value.dsHocSinhDiem.find { it.maHocSinh == maHocSinh }
        val oldVal = studentItem?.diemGiuaKy

        val applyAction = {
            _uiState.update { currentState ->
                val updatedList = currentState.dsHocSinhDiem.map { item ->
                    if (item.maHocSinh == maHocSinh) {
                        val newDiemTB = tinhDiemUseCase.tinhDiemTBMonHK(
                            dsDiemTX = item.dsDiemTX,
                            diemGK = value,
                            diemCK = item.diemCuoiKy
                        )
                        item.copy(
                            diemGiuaKy = value,
                            diemTBMon = newDiemTB
                        )
                    } else {
                        item
                    }
                }
                currentState.copy(dsHocSinhDiem = updatedList, pendingGradeChange = null)
            }
        }

        if (oldVal != null) {
            _uiState.update {
                it.copy(
                    pendingGradeChange = PendingGradeChange(
                        maHocSinh = maHocSinh,
                        loaiDiem = "Giữa kỳ (ĐGK)",
                        diemCu = oldVal,
                        diemMoi = value,
                        onConfirm = { reason ->
                            viewModelScope.launch {
                                lichSuSuaDiemRepository.logGradeChange(
                                    LichSuSuaDiemEntity(
                                        maHocSinh = maHocSinh,
                                        maMonHoc = _uiState.value.currentMaMonHoc,
                                        loaiDiem = "Giữa kỳ (ĐGK)",
                                        diemCu = oldVal,
                                        diemMoi = value,
                                        lyDoSua = reason,
                                        nguoiSua = "Giáo viên bộ môn"
                                    )
                                )
                                applyAction()
                            }
                        }
                    )
                )
            }
        } else {
            applyAction()
        }
    }

    fun updateDiemCK(maHocSinh: String, value: Float) {
        if (_uiState.value.isGradeLocked) return

        val studentItem = _uiState.value.dsHocSinhDiem.find { it.maHocSinh == maHocSinh }
        val oldVal = studentItem?.diemCuoiKy

        val applyAction = {
            _uiState.update { currentState ->
                val updatedList = currentState.dsHocSinhDiem.map { item ->
                    if (item.maHocSinh == maHocSinh) {
                        val newDiemTB = tinhDiemUseCase.tinhDiemTBMonHK(
                            dsDiemTX = item.dsDiemTX,
                            diemGK = item.diemGiuaKy,
                            diemCK = value
                        )
                        item.copy(
                            diemCuoiKy = value,
                            diemTBMon = newDiemTB
                        )
                    } else {
                        item
                    }
                }
                currentState.copy(dsHocSinhDiem = updatedList, pendingGradeChange = null)
            }
        }

        if (oldVal != null) {
            _uiState.update {
                it.copy(
                    pendingGradeChange = PendingGradeChange(
                        maHocSinh = maHocSinh,
                        loaiDiem = "Cuối kỳ (ĐCK)",
                        diemCu = oldVal,
                        diemMoi = value,
                        onConfirm = { reason ->
                            viewModelScope.launch {
                                lichSuSuaDiemRepository.logGradeChange(
                                    LichSuSuaDiemEntity(
                                        maHocSinh = maHocSinh,
                                        maMonHoc = _uiState.value.currentMaMonHoc,
                                        loaiDiem = "Cuối kỳ (ĐCK)",
                                        diemCu = oldVal,
                                        diemMoi = value,
                                        lyDoSua = reason,
                                        nguoiSua = "Giáo viên bộ môn"
                                    )
                                )
                                applyAction()
                            }
                        }
                    )
                )
            }
        } else {
            applyAction()
        }
    }

    fun saveBangDiem() {
        val currentState = _uiState.value
        if (currentState.isGradeLocked) return

        val entities = currentState.dsHocSinhDiem.map { item ->
            BangDiemEntity(
                maHocSinh = item.maHocSinh,
                maMonHoc = currentState.currentMaMonHoc,
                hocKy = currentState.currentHocKy,
                namHoc = currentState.currentNamHoc,
                dsDiemTX = item.dsDiemTX,
                diemGiuaKy = item.diemGiuaKy,
                diemCuoiKy = item.diemCuoiKy,
                nhanXet = item.nhanXet
            )
        }
        viewModelScope.launch {
            try {
                repository.saveBangDiemList(entities)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessage = e.localizedMessage ?: "Failed to save bang diem")
                }
            }
        }
    }
}
