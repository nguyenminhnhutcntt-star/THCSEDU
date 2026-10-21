package com.example.thcsedu.ui.nepnep

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.entity.ViPhamEntity
import com.example.thcsedu.domain.repository.ViPhamRepository
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
    private val viPhamRepository: ViPhamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChamNeNepUiState())
    val uiState: StateFlow<ChamNeNepUiState> = _uiState.asStateFlow()

    fun loadViPham(maLop: String) {
        _uiState.update { it.copy(maLop = maLop, isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            viPhamRepository.getViPhamByLop(maLop)
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading violations") }
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
        if (maHocSinh.isBlank() || tenLoi.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập mã học sinh và lỗi vi phạm") }
            return
        }

        viewModelScope.launch {
            try {
                val entity = ViPhamEntity(
                    maHocSinh = maHocSinh.trim().uppercase(),
                    maLop = maLop,
                    ngayViPham = ngayViPham,
                    tenLoi = tenLoi.trim(),
                    diemTru = diemTru
                )
                viPhamRepository.saveViPham(entity)
                _uiState.update { it.copy(successMessage = "Đã ghi nhận vi phạm thành công!", errorMessage = null) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Ghi nhận thất bại") }
            }
        }
    }
}
