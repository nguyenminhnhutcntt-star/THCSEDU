package com.example.thcsedu.ui.nepnep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.dao.HocSinhDao
import com.example.thcsedu.data.local.entity.ViPhamEntity
import com.example.thcsedu.domain.repository.ViPhamRepository
import com.example.thcsedu.ui.util.NotificationHelper
import com.example.thcsedu.ui.util.ValidationUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChamNeNepUiState(
    val maLop: String = "9A1",
    val ngayViPham: String = "2026-10-13",
    val viPhamList: List<ViPhamEntity> = emptyList(),
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class ChamNeNepViewModel @Inject constructor(
    private val viPhamRepository: ViPhamRepository,
    private val hocSinhDao: HocSinhDao,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChamNeNepUiState())
    val uiState: StateFlow<ChamNeNepUiState> = _uiState.asStateFlow()

    fun loadViPham(maLop: String) {
        _uiState.update { it.copy(maLop = maLop, isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            viPhamRepository.getViPhamByLop(maLop)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Lỗi tải danh sách vi phạm") }
                }
                .collect { list ->
                    _uiState.update { it.copy(isLoading = false, viPhamList = list) }
                }
        }
    }

    fun logViPham(
        maHocSinh: String,
        maLop: String,
        ngayViPham: String,
        tenLoi: String,
        diemTru: Int,
        onSuccess: () -> Unit
    ) {
        val trimmedMaHS = maHocSinh.trim().uppercase()
        val trimmedDate = ngayViPham.trim()

        if (trimmedMaHS.isBlank() || tenLoi.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ mã học sinh và lỗi vi phạm") }
            return
        }

        // 1. Kiểm tra ngày hợp lệ
        if (!ValidationUtil.isValidDate(trimmedDate)) {
            _uiState.update { it.copy(errorMessage = "Ngày vi phạm không hợp lệ ($trimmedDate). Định dạng hợp lệ: yyyy-MM-dd hoặc dd/MM/yyyy") }
            return
        }

        viewModelScope.launch {
            // 2. Kiểm tra mã học sinh phải tồn tại trong CSDL
            val student = hocSinhDao.getHocSinhByIdOnce(trimmedMaHS)
            if (student == null) {
                _uiState.update { it.copy(errorMessage = "Mã học sinh $trimmedMaHS không tồn tại trong hệ thống!") }
                return@launch
            }

            try {
                val entity = ViPhamEntity(
                    maHocSinh = trimmedMaHS,
                    maLop = maLop,
                    ngayViPham = trimmedDate,
                    tenLoi = tenLoi.trim(),
                    diemTru = diemTru
                )
                viPhamRepository.saveViPham(entity)

                // 3. Thông báo đẩy khi có vi phạm
                notificationHelper.notifyViPham(
                    studentName = student.hoTen,
                    violation = tenLoi.trim(),
                    deducted = diemTru
                )

                _uiState.update { it.copy(successMessage = "Đã ghi nhận vi phạm cho học sinh ${student.hoTen} thành công!", errorMessage = null) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Ghi nhận thất bại") }
            }
        }
    }
}
