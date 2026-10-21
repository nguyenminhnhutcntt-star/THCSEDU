package com.example.thcsedu.ui.qrcode

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class QuetMaQrUiState(
    val lastScannedResult: String? = null,
    val isSuccess: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class QuetMaQrViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(QuetMaQrUiState())
    val uiState: StateFlow<QuetMaQrUiState> = _uiState.asStateFlow()

    fun onQrScanned(rawCode: String) {
        _uiState.value = QuetMaQrUiState(
            lastScannedResult = rawCode,
            isSuccess = true,
            message = "Quét thành công mã: $rawCode"
        )
    }

    fun clearResult() {
        _uiState.value = QuetMaQrUiState()
    }
}
