package com.example.thcsedu.ui.parent

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

data class SubjectScoreItem(
    val tenMonHoc: String,
    val diemTB: Float,
    val danhGia: String
)

data class SemesterScore(
    val hocKy: String,
    val diemTrungBinhChung: Float
)

data class ParentDashboardUiState(
    val studentName: String = "Nguyễn Văn An",
    val className: String = "Lớp 9A1",
    val conduct: String = "Tốt",
    val subjectScores: List<SubjectScoreItem> = listOf(
        SubjectScoreItem("Toán", 8.5f, "Giỏi"),
        SubjectScoreItem("Ngữ Văn", 7.8f, "Khá"),
        SubjectScoreItem("Tiếng Anh", 9.0f, "Xuất sắc"),
        SubjectScoreItem("Khoa học Tự nhiên", 8.2f, "Giỏi"),
        SubjectScoreItem("Lịch sử & Địa lý", 8.0f, "Giỏi"),
        SubjectScoreItem("Tin học", 9.5f, "Xuất sắc"),
        SubjectScoreItem("Công nghệ", 8.8f, "Giỏi"),
        SubjectScoreItem("Giáo dục công dân", 8.5f, "Giỏi")
    ),
    val semesterHistory: List<SemesterScore> = listOf(
        SemesterScore("HK1 23-24", 7.8f),
        SemesterScore("HK2 23-24", 8.0f),
        SemesterScore("HK1 24-25", 8.2f),
        SemesterScore("HK2 24-25", 8.4f)
    ),
    val violations: List<ViPhamEntity> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class ParentDashboardViewModel @Inject constructor(
    private val viPhamRepository: ViPhamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParentDashboardUiState())
    val uiState: StateFlow<ParentDashboardUiState> = _uiState.asStateFlow()

    init {
        loadStudentViolations("HS01")
    }

    private fun loadStudentViolations(maHocSinh: String) {
        viewModelScope.launch {
            viPhamRepository.getViPhamByHocSinh(maHocSinh)
                .catch { /* ignore */ }
                .collect { list ->
                    _uiState.update { it.copy(violations = list) }
                }
        }
    }
}
