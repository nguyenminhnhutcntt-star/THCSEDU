package com.example.thcsedu.ui.thongbao

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.entity.ThongBaoEntity
import com.example.thcsedu.domain.repository.ThongBaoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ThongBaoUiState(
    val thongBaoList: List<ThongBaoEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class ThongBaoViewModel @Inject constructor(
    private val thongBaoRepository: ThongBaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ThongBaoUiState())
    val uiState: StateFlow<ThongBaoUiState> = _uiState.asStateFlow()

    init {
        loadThongBao()
    }

    fun loadThongBao() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            thongBaoRepository.getAllThongBao()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading announcements") }
                }
                .collect { list ->
                    _uiState.update { it.copy(isLoading = false, thongBaoList = list) }
                }
        }
    }

    fun postThongBao(tieuDe: String, noiDung: String, nguoiGui: String, onSuccess: () -> Unit) {
        if (tieuDe.isBlank() || noiDung.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Tiêu đề và nội dung không được để trống") }
            return
        }

        viewModelScope.launch {
            try {
                val entity = ThongBaoEntity(
                    tieuDe = tieuDe.trim(),
                    noiDung = noiDung.trim(),
                    ngayGui = "2026-10-13",
                    nguoiGui = nguoiGui.trim()
                )
                thongBaoRepository.saveThongBao(entity)
                _uiState.update { it.copy(successMessage = "Đăng thông báo thành công!", errorMessage = null) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Đăng thông báo thất bại") }
            }
        }
    }
}
