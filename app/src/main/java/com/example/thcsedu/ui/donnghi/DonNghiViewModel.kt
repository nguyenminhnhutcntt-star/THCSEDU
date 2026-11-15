package com.example.thcsedu.ui.donnghi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.dao.DonXinNghiDao
import com.example.thcsedu.data.local.dao.HocSinhDao
import com.example.thcsedu.data.local.dao.SuatAnDao
import com.example.thcsedu.data.local.entity.DonXinNghiEntity
import com.example.thcsedu.domain.repository.DonXinNghiRepository
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

data class DonNghiUiState(
    val donNghiList: List<DonXinNghiEntity> = emptyList(),
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class DonNghiViewModel @Inject constructor(
    private val repository: DonXinNghiRepository,
    private val donXinNghiDao: DonXinNghiDao,
    private val suatAnDao: SuatAnDao,
    private val hocSinhDao: HocSinhDao,
    private val notificationHelper: NotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(DonNghiUiState())
    val uiState: StateFlow<DonNghiUiState> = _uiState.asStateFlow()

    init {
        loadAllDon()
    }

    fun loadAllDon() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            repository.getAllDonXinNghi()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Lỗi tải đơn xin nghỉ") }
                }
                .collect { list ->
                    _uiState.update { it.copy(isLoading = false, donNghiList = list) }
                }
        }
    }

    fun submitDon(maHocSinh: String, tuNgay: String, denNgay: String, lyDo: String, onSuccess: () -> Unit) {
        val trimmedMaHS = maHocSinh.trim().uppercase()
        val trimmedTuNgay = tuNgay.trim()
        val trimmedDenNgay = denNgay.trim()
        val trimmedLyDo = lyDo.trim()

        if (trimmedMaHS.isBlank() || trimmedTuNgay.isBlank() || trimmedDenNgay.isBlank() || trimmedLyDo.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng điền đầy đủ thông tin đơn xin nghỉ") }
            return
        }

        // 1. Kiểm tra ngày hợp lệ
        if (!ValidationUtil.isValidDate(trimmedTuNgay)) {
            _uiState.update { it.copy(errorMessage = "Ngày bắt đầu không hợp lệ ($trimmedTuNgay). Định dạng: yyyy-MM-dd hoặc dd/MM/yyyy") }
            return
        }
        if (!ValidationUtil.isValidDate(trimmedDenNgay)) {
            _uiState.update { it.copy(errorMessage = "Ngày kết thúc không hợp lệ ($trimmedDenNgay). Định dạng: yyyy-MM-dd hoặc dd/MM/yyyy") }
            return
        }
        if (!ValidationUtil.isValidDateRange(trimmedTuNgay, trimmedDenNgay)) {
            _uiState.update { it.copy(errorMessage = "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc!") }
            return
        }

        viewModelScope.launch {
            // 2. Kiểm tra mã học sinh phải tồn tại
            val student = hocSinhDao.getHocSinhByIdOnce(trimmedMaHS)
            if (student == null) {
                _uiState.update { it.copy(errorMessage = "Mã học sinh $trimmedMaHS không tồn tại trong hệ thống!") }
                return@launch
            }

            try {
                val entity = DonXinNghiEntity(
                    maHocSinh = trimmedMaHS,
                    tuNgay = trimmedTuNgay,
                    denNgay = trimmedDenNgay,
                    lyDo = trimmedLyDo,
                    trangThai = "CHO_DUYET"
                )
                repository.saveDon(entity)

                // 3. Thông báo đẩy khi có đơn xin nghỉ mới
                notificationHelper.notifyDonXinNghi(
                    studentName = student.hoTen,
                    reason = trimmedLyDo,
                    dates = "$trimmedTuNgay đến $trimmedDenNgay"
                )

                _uiState.update { it.copy(successMessage = "Gửi đơn xin nghỉ cho em ${student.hoTen} thành công!", errorMessage = null) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Gửi đơn thất bại") }
            }
        }
    }

    fun updateStatus(id: Long, trangThai: String) {
        viewModelScope.launch {
            try {
                repository.updateStatus(id, trangThai)
                if (trangThai == "DA_DUYET") {
                    val don = donXinNghiDao.getDonById(id)
                    if (don != null) {
                        suatAnDao.updateTrangThaiByDateRange(don.maHocSinh, don.tuNgay, don.denNgay, "DA_HUY")
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Cập nhật trạng thái thất bại") }
            }
        }
    }
}
