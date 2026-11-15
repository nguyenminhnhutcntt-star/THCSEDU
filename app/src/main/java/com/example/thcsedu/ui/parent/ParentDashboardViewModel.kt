package com.example.thcsedu.ui.parent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.dao.BangDiemDao
import com.example.thcsedu.data.local.dao.HocSinhDao
import com.example.thcsedu.data.local.dao.MonHocDao
import com.example.thcsedu.data.local.entity.ViPhamEntity
import com.example.thcsedu.data.preferences.UserPreferencesRepository
import com.example.thcsedu.domain.model.XepLoaiEnum
import com.example.thcsedu.domain.repository.ViPhamRepository
import com.example.thcsedu.domain.usecase.TinhDiemVaXepLoaiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

data class SubjectScoreItem(
    val maMonHoc: String,
    val tenMonHoc: String,
    val diemTX: List<Float> = emptyList(),
    val diemGK: Float? = null,
    val diemCK: Float? = null,
    val diemTB: Float = 0f,
    val danhGia: String = "Đạt",
    val nhanXet: String? = null
)

data class SemesterScore(
    val hocKy: String,
    val diemTrungBinhChung: Float
)

data class ParentDashboardUiState(
    val studentId: String = "HS01",
    val studentName: String = "Nguyễn Văn An",
    val className: String = "9A1",
    val conduct: String = "Tốt",
    val academicRank: String = "Tốt", // Thông tư 22: Tốt / Khá / Đạt / Chưa đạt
    val gpa: Float = 8.5f,
    val subjectScores: List<SubjectScoreItem> = emptyList(),
    val semesterHistory: List<SemesterScore> = emptyList(),
    val violations: List<ViPhamEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ParentDashboardViewModel @Inject constructor(
    private val hocSinhDao: HocSinhDao,
    private val bangDiemDao: BangDiemDao,
    private val monHocDao: MonHocDao,
    private val viPhamRepository: ViPhamRepository,
    private val tinhDiemUseCase: TinhDiemVaXepLoaiUseCase,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParentDashboardUiState())
    val uiState: StateFlow<ParentDashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val maDinhDanh = userPreferencesRepository.maDinhDanhFlow.firstOrNull()
            val studentId = if (!maDinhDanh.isNullOrBlank()) maDinhDanh else "HS01"
            loadStudentData(studentId)
        }
    }

    fun loadStudentData(maHocSinh: String) {
        _uiState.update { it.copy(studentId = maHocSinh, isLoading = true) }

        // 1. Tải danh sách vi phạm
        viewModelScope.launch {
            viPhamRepository.getViPhamByHocSinh(maHocSinh)
                .catch { /* ignore */ }
                .collect { list ->
                    _uiState.update { it.copy(violations = list) }
                }
        }

        // 2. Tải thông tin học sinh, môn học và điểm số từ Room DB
        viewModelScope.launch {
            combine(
                hocSinhDao.getHocSinhById(maHocSinh),
                bangDiemDao.getBangDiemByHocSinh(maHocSinh),
                monHocDao.getAllMonHoc()
            ) { student, bangDiemList, monHocList ->
                val subjectMap = monHocList.associateBy { it.maMonHoc }

                val subjectScoreItems = bangDiemList.map { grade ->
                    val subject = subjectMap[grade.maMonHoc]
                    val subjectName = subject?.tenMonHoc ?: grade.maMonHoc

                    val calculatedDTB = tinhDiemUseCase.tinhDiemTBMonHK(
                        dsDiemTX = grade.dsDiemTX,
                        diemGK = grade.diemGiuaKy,
                        diemCK = grade.diemCuoiKy
                    ) ?: 0.0f

                    // Đánh giá môn học theo Thông tư 22
                    val rating = when {
                        calculatedDTB >= 8.0f -> "Tốt"
                        calculatedDTB >= 6.5f -> "Khá"
                        calculatedDTB >= 5.0f -> "Đạt"
                        else -> "Chưa đạt"
                    }

                    SubjectScoreItem(
                        maMonHoc = grade.maMonHoc,
                        tenMonHoc = subjectName,
                        diemTX = grade.dsDiemTX,
                        diemGK = grade.diemGiuaKy,
                        diemCK = grade.diemCuoiKy,
                        diemTB = calculatedDTB,
                        danhGia = rating,
                        nhanXet = grade.nhanXet
                    )
                }

                // Tính điểm trung bình chung (GPA)
                val totalScores = subjectScoreItems.map { it.diemTB }.filter { it > 0f }
                val overallGpa = if (totalScores.isNotEmpty()) {
                    val sum = totalScores.fold(BigDecimal.ZERO) { acc, sc -> acc.add(BigDecimal(sc.toString())) }
                    sum.divide(BigDecimal(totalScores.size), 1, RoundingMode.HALF_UP).toFloat()
                } else {
                    0.0f
                }

                // Xếp loại học lực 4 mức (Tốt / Khá / Đạt / Chưa đạt) theo Thông tư 22
                val dsDTB = subjectScoreItems.map { it.diemTB }
                val dsNhanXet = subjectScoreItems.map { it.diemTB >= 5.0f }
                val xepLoaiEnum = if (dsDTB.isNotEmpty()) {
                    tinhDiemUseCase.xepLoaiHocLuc(dsDTB, dsNhanXet)
                } else {
                    XepLoaiEnum.DAT
                }

                val rankDisplay = when (xepLoaiEnum) {
                    XepLoaiEnum.TOT -> "Tốt"
                    XepLoaiEnum.KHA -> "Khá"
                    XepLoaiEnum.DAT -> "Đạt"
                    XepLoaiEnum.CHUA_DAT -> "Chưa đạt"
                }

                // Lịch sử điểm học kỳ
                val semesterHistory = listOf(
                    SemesterScore("HK1 23-24", 7.8f),
                    SemesterScore("HK2 23-24", 8.0f),
                    SemesterScore("HK1 24-25", overallGpa),
                    SemesterScore("Dự kiến", (overallGpa + 0.2f).coerceAtMost(10.0f))
                )

                _uiState.update {
                    it.copy(
                        studentId = maHocSinh,
                        studentName = student?.hoTen ?: "Nguyễn Văn An",
                        className = student?.maLop ?: "9A1",
                        conduct = "Tốt",
                        academicRank = rankDisplay,
                        gpa = overallGpa,
                        subjectScores = subjectScoreItems,
                        semesterHistory = semesterHistory,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Lỗi tải dữ liệu học sinh"
                    )
                }
            }.collect { }
        }
    }
}
