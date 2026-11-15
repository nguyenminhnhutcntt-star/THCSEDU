package com.example.thcsedu.ui.qrcode

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.dao.CheckInCongTruongDao
import com.example.thcsedu.data.local.dao.HocSinhDao
import com.example.thcsedu.data.local.entity.CheckInCongTruongEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class QuetMaQrUiState(
    val lastScannedResult: String? = null,
    val isSuccess: Boolean = false,
    val message: String? = null,
    val recentCheckIns: List<CheckInCongTruongEntity> = emptyList(),
    val isProcessing: Boolean = false
)

@HiltViewModel
class QuetMaQrViewModel @Inject constructor(
    private val hocSinhDao: HocSinhDao,
    private val checkInDao: CheckInCongTruongDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuetMaQrUiState())
    val uiState: StateFlow<QuetMaQrUiState> = _uiState.asStateFlow()

    init {
        loadRecentCheckIns()
    }

    private fun loadRecentCheckIns() {
        viewModelScope.launch {
            checkInDao.getRecentCheckIns(50)
                .catch { /* ignore */ }
                .collect { list ->
                    _uiState.update { it.copy(recentCheckIns = list) }
                }
        }
    }

    fun onQrScanned(rawCode: String) {
        val trimmedCode = rawCode.trim().uppercase()
        if (trimmedCode.isBlank()) return

        // Tránh quét trùng lặp liên tục trong 2 giây
        if (_uiState.value.isProcessing) return
        _uiState.update { it.copy(isProcessing = true) }

        viewModelScope.launch {
            try {
                // 1. Kiểm tra mã học sinh tồn tại trong cơ sở dữ liệu
                val student = hocSinhDao.getHocSinhByIdOnce(trimmedCode)
                if (student == null) {
                    _uiState.update {
                        it.copy(
                            lastScannedResult = trimmedCode,
                            isSuccess = false,
                            message = "Mã học sinh '$trimmedCode' không tồn tại trong hệ thống!",
                            isProcessing = false
                        )
                    }
                    return@launch
                }

                // 2. Ghi lại giờ vào cổng
                val now = Date()
                val timeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val timeString = timeFormat.format(now)

                // Kiểm tra đúng giờ hay đi muộn (Quy định vào cổng trước 07:15)
                val cal = Calendar.getInstance().apply { time = now }
                val hour = cal.get(Calendar.HOUR_OF_DAY)
                val minute = cal.get(Calendar.MINUTE)
                val isLate = (hour > 7) || (hour == 7 && minute > 15)
                val status = if (isLate) "Đi muộn" else "Đúng giờ"

                val checkInRecord = CheckInCongTruongEntity(
                    maHocSinh = student.maHocSinh,
                    hoTen = student.hoTen,
                    maLop = student.maLop,
                    thoiGianVao = timeString,
                    trangThai = status
                )

                checkInDao.insertCheckIn(checkInRecord)

                _uiState.update {
                    it.copy(
                        lastScannedResult = student.maHocSinh,
                        isSuccess = true,
                        message = "Đã ghi nhận vào cổng: ${student.hoTen} (${student.maLop}) - $status lúc $timeString",
                        isProcessing = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSuccess = false,
                        message = "Lỗi ghi nhận vào cổng: ${e.localizedMessage}",
                        isProcessing = false
                    )
                }
            }
        }
    }

    fun clearResult() {
        _uiState.update { it.copy(message = null, lastScannedResult = null, isProcessing = false) }
    }
}
