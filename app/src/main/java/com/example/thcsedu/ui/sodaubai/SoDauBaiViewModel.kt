package com.example.thcsedu.ui.sodaubai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.entity.SoDauBaiEntity
import com.example.thcsedu.domain.repository.SoDauBaiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SoDauBaiViewModel @Inject constructor(
    private val repository: SoDauBaiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SoDauBaiUiState())
    val uiState: StateFlow<SoDauBaiUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadSoDauBai(maLop: String, ngay: String) {
        _uiState.update { it.copy(maLop = maLop, ngay = ngay, isLoading = true, errorMessage = null) }
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            repository.getSoDauBaiByLopAndDate(maLop, ngay)
                .catch { e ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Error loading class journal")
                    }
                }
                .collect { list ->
                    _uiState.update {
                        it.copy(isLoading = false, entries = list)
                    }
                }
        }
    }

    fun updateClassAndDate(maLop: String, ngay: String) {
        loadSoDauBai(maLop, ngay)
    }

    fun openEditDialog(tietHoc: Int) {
        _uiState.update { it.copy(selectedPeriod = tietHoc, isEditing = true) }
    }

    fun closeEditDialog() {
        _uiState.update { it.copy(selectedPeriod = null, isEditing = false) }
    }

    fun saveEntry(
        maLop: String,
        ngay: String,
        tietHoc: Int,
        tenMon: String,
        tenBaiDay: String,
        nhanXet: String,
        xepLoaiTiet: String,
        giaoVienId: String,
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val existing = _uiState.value.entries.find { it.tietHoc == tietHoc }
                val entity = SoDauBaiEntity(
                    id = existing?.id ?: 0L,
                    maLop = maLop,
                    ngay = ngay,
                    tietHoc = tietHoc,
                    tenMon = tenMon,
                    tenBaiDay = tenBaiDay,
                    nhanXet = nhanXet,
                    xepLoaiTiet = xepLoaiTiet,
                    giaoVienId = giaoVienId
                )
                repository.saveSoDauBai(entity)
                closeEditDialog()
                onComplete()
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.localizedMessage ?: "Failed to save journal entry") }
            }
        }
    }
}
