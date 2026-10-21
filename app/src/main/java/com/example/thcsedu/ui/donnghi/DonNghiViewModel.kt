package com.example.thcsedu.ui.donnghi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.dao.DonXinNghiDao
import com.example.thcsedu.data.local.dao.SuatAnDao
import com.example.thcsedu.data.local.entity.DonXinNghiEntity
import com.example.thcsedu.domain.repository.DonXinNghiRepository
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
    private val suatAnDao: SuatAnDao
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
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading leave requests") }
                }
                .collect { list ->
                    _uiState.update { it.copy(isLoading = false, donNghiList = list) }
                }
        }
    }

    fun submitDon(maHocSinh: String, tuNgay: String, denNgay: String, lyDo: String, onSuccess: () -> Unit) {
        if (maHocSinh.isBlank() || tuNgay.isBlank() || denNgay.isBlank() || lyDo.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng điền đầy đủ thông tin đơn xin nghỉ") }
            return
        }

        viewModelScope.launch {
            try {
                val entity = DonXinNghiEntity(
                    maHocSinh = maHocSinh.trim(),
                    tuNgay = tuNgay.trim(),
                    denNgay = denNgay.trim(),
                    lyDo = lyDo.trim(),
                    trangThai = "CHO_DUYET"
                )
                repository.saveDon(entity)
                _uiState.update { it.copy(successMessage = "Gửi đơn xin nghỉ thành công!", errorMessage = null) }
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
