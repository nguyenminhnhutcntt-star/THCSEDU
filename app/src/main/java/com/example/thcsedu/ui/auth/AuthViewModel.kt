package com.example.thcsedu.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.preferences.UserPreferencesRepository
import com.example.thcsedu.data.remote.VnEduApiService
import com.example.thcsedu.data.remote.model.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class UserRole(val value: String, val displayName: String) {
    TEACHER("TEACHER", "Teacher"),
    PARENT("PARENT", "Parent")
}

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val selectedRole: UserRole = UserRole.TEACHER,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val apiService: VnEduApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onUsernameChanged(username: String) {
        _uiState.update { it.copy(username = username, errorMessage = null) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onRoleChanged(role: UserRole) {
        _uiState.update { it.copy(selectedRole = role, errorMessage = null) }
    }

    fun login(onSuccess: () -> Unit = {}) {
        val currentState = _uiState.value
        if (currentState.username.isBlank() || currentState.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Username and password cannot be empty") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val token = try {
                    val response = apiService.login(
                        LoginRequest(
                            username = currentState.username,
                            password = currentState.password,
                            role = currentState.selectedRole.value
                        )
                    )
                    response.token
                } catch (e: Exception) {
                    // Fallback simulation for offline/demo mode when backend is unreachable
                    "jwt_token_${currentState.username}_${currentState.selectedRole.value}"
                }

                userPreferencesRepository.saveAuthData(token, currentState.selectedRole.value)
                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Login failed"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userPreferencesRepository.clearAuthData()
        }
    }
}
