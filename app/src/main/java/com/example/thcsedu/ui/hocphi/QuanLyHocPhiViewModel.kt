package com.example.thcsedu.ui.hocphi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.entity.HoaDonHocPhiEntity
import com.example.thcsedu.domain.repository.HoaDonHocPhiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuanLyHocPhiUiState(
    val maHocSinh: String = "HS01",
    val hoaDonList: List<HoaDonHocPhiEntity> = emptyList(),
    val totalUnpaid: Double = 0.0,
    val isLoading: Boolean = false,
    val selectedBillForQR: HoaDonHocPhiEntity? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class QuanLyHocPhiViewModel @Inject constructor(
    private val hoaDonRepository: HoaDonHocPhiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuanLyHocPhiUiState())
    val uiState: StateFlow<QuanLyHocPhiUiState> = _uiState.asStateFlow()

    init {
        loadHoaDon("HS01")
    }

    fun loadHoaDon(maHocSinh: String) {
        _uiState.update { it.copy(maHocSinh = maHocSinh, isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            hoaDonRepository.getHoaDonByHocSinh(maHocSinh)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading tuition bills") }
                }
                .collect { list ->
                    val unpaid = list.filter { !it.daThanhToan }.sumOf { it.soTien }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            hoaDonList = list,
                            totalUnpaid = unpaid
                        )
                    }
                }
        }
    }

    fun showQrModal(bill: HoaDonHocPhiEntity) {
        _uiState.update { it.copy(selectedBillForQR = bill) }
    }

    fun dismissQrModal() {
        _uiState.update { it.copy(selectedBillForQR = null) }
    }

    fun payBill(id: Long) {
        viewModelScope.launch {
            try {
                hoaDonRepository.updatePaymentStatus(id, true)
                _uiState.update {
                    it.copy(
                        selectedBillForQR = null,
                        successMessage = "Thanh toán thành công qua QR Code!"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Thanh toán thất bại") }
            }
        }
    }
}
