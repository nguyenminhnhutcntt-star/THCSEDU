package com.example.thcsedu.ui.thehocsinh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.dao.HocSinhDao
import com.example.thcsedu.data.local.entity.HocSinhEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TheHocSinhUiState(
    val student: HocSinhEntity? = null,
    val isLoading: Boolean = false,
    val isQrExpanded: Boolean = false
)

@HiltViewModel
class TheHocSinhViewModel @Inject constructor(
    private val hocSinhDao: HocSinhDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(TheHocSinhUiState())
    val uiState: StateFlow<TheHocSinhUiState> = _uiState.asStateFlow()

    init {
        loadStudent("HS01")
    }

    fun loadStudent(maHocSinh: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val student = hocSinhDao.getHocSinhByIdOnce(maHocSinh)
            _uiState.update { it.copy(student = student, isLoading = false) }
        }
    }

    fun setQrExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isQrExpanded = expanded) }
    }
}
