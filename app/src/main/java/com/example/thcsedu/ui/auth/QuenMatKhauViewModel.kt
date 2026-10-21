package com.example.thcsedu.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuenMatKhauUiState(
    val username: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

@HiltViewModel
class QuenMatKhauViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(QuenMatKhauUiState())
    val uiState: StateFlow<QuenMatKhauUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username, errorMessage = null) }
    }

    fun onNewPasswordChanged(password: String) {
        _uiState.update { it.copy(newPassword = password, errorMessage = null) }
    }

    fun onConfirmPasswordChanged(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorMessage = null) }
    }

    fun resetPassword(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.username.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập tên đăng nhập / tài khoản") }
            return
        }
        if (state.newPassword.length < 4) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu mới phải có ít nhất 4 ký tự") }
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Mật khẩu xác nhận không khớp") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            delay(800)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    successMessage = "Đổi mật khẩu thành công! Vui lòng đăng nhập lại."
                )
            }
            delay(1200)
            onSuccess()
        }
    }
}
