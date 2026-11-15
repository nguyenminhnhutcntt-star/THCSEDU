package com.example.thcsedu.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.dao.TaiKhoanDao
import com.example.thcsedu.data.preferences.UserPreferencesRepository
import com.example.thcsedu.ui.util.PasswordUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UserRole(val value: String, val displayName: String) {
    TEACHER("TEACHER", "Giáo viên"),
    PARENT("PARENT", "Phụ huynh"),
    ADMIN("ADMIN", "Quản trị viên")
}

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val authenticatedRole: UserRole? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val taiKhoanDao: TaiKhoanDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun quickFill(username: String, pass: String) {
        _uiState.update { it.copy(username = username, password = pass, errorMessage = null) }
    }

    fun login(onSuccess: (UserRole) -> Unit = {}) {
        val currentState = _uiState.value
        val username = currentState.username.trim()
        val password = currentState.password.trim()

        if (username.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val account = taiKhoanDao.getTaiKhoanByUsername(username)
                if (account == null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Tài khoản không tồn tại trên hệ thống"
                        )
                    }
                    return@launch
                }

                // Kiểm tra băm mật khẩu
                val isPasswordCorrect = PasswordUtil.verifyPassword(password, account.matKhauHash)
                if (!isPasswordCorrect) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Mật khẩu không chính xác"
                        )
                    }
                    return@launch
                }

                // Vai trò được xác định từ tài khoản trong CSDL
                val userRole = when (account.vaiTro.uppercase()) {
                    "PARENT" -> UserRole.PARENT
                    "ADMIN" -> UserRole.ADMIN
                    else -> UserRole.TEACHER
                }

                val token = "token_${account.tenDangNhap}_${account.vaiTro}_${System.currentTimeMillis()}"
                userPreferencesRepository.saveAuthData(
                    token = token,
                    role = userRole.value,
                    username = account.tenDangNhap,
                    maDinhDanh = account.maDinhDanh,
                    hoTen = account.hoTen
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        authenticatedRole = userRole,
                        errorMessage = null
                    )
                }

                onSuccess(userRole)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Đăng nhập thất bại"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearAuthData()
            _uiState.update { AuthUiState() }
        }
    }
}
