package com.example.thcsedu.ui.dashboard

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

data class TeacherDashboardUiState(
    val selectedClass: String = "9A1",
    val violations: List<ViPhamEntity> = emptyList(),
    val weeklyMeritScore: Int = 100
)

@HiltViewModel
class TeacherDashboardViewModel @Inject constructor(
    private val viPhamRepository: ViPhamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherDashboardUiState())
    val uiState: StateFlow<TeacherDashboardUiState> = _uiState.asStateFlow()

    init {
        loadClassData("9A1")
    }

    fun loadClassData(maLop: String) {
        viewModelScope.launch {
            viPhamRepository.getViPhamByLop(maLop)
                .catch { /* ignore */ }
                .collect { list ->
                    val totalDeducted = list.sumOf { it.diemTru }
                    val score = (100 - totalDeducted).coerceAtLeast(0)
                    _uiState.update {
                        it.copy(
                            selectedClass = maLop,
                            violations = list,
                            weeklyMeritScore = score
                        )
                    }
                }
        }
    }
}
