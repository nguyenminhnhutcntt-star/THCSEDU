package com.example.thcsedu.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.dao.HocSinhDao
import com.example.thcsedu.data.local.dao.ThoiKhoaBieuDao
import com.example.thcsedu.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TimetableCell(
    val subjectName: String?,
    val teacher: String?,
    val room: String? = null
)

data class TimetableUiState(
    val maLop: String = "9A1",
    val studentName: String? = null,
    val schedule: Map<Pair<Int, Int>, TimetableCell> = emptyMap(),
    val selectedCell: TimetableCell? = null,
    val selectedCellInfo: String? = null,
    val isLoading: Boolean = false
)

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val thoiKhoaBieuDao: ThoiKhoaBieuDao,
    private val hocSinhDao: HocSinhDao,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimetableUiState())
    val uiState: StateFlow<TimetableUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val role = userPreferencesRepository.roleFlow.firstOrNull()
            val maDinhDanh = userPreferencesRepository.maDinhDanhFlow.firstOrNull()

            if (role == "PARENT" && !maDinhDanh.isNullOrBlank()) {
                loadTimetableForStudent(maDinhDanh)
            } else {
                loadTimetableForClass("9A1")
            }
        }
    }

    fun loadTimetableForStudent(maHocSinh: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val student = hocSinhDao.getHocSinhByIdOnce(maHocSinh)
            val maLop = student?.maLop ?: "9A1"
            _uiState.update { it.copy(studentName = student?.hoTen, maLop = maLop) }
            loadTimetableForClass(maLop)
        }
    }

    fun loadTimetableForClass(maLop: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, maLop = maLop) }
            thoiKhoaBieuDao.getThoiKhoaBieuByLop(maLop).collect { list ->
                val map = mutableMapOf<Pair<Int, Int>, TimetableCell>()
                for (item in list) {
                    val dayCol = item.thu - 1 // 2 -> 1 (Thứ 2), ..., 7 -> 6 (Thứ 7)
                    map[Pair(dayCol, item.tiet)] = TimetableCell(
                        subjectName = item.tenMonHoc,
                        teacher = item.giaoVien,
                        room = item.phongHoc
                    )
                }
                _uiState.update { it.copy(schedule = map, isLoading = false) }
            }
        }
    }

    fun onCellClicked(day: Int, period: Int) {
        val cell = uiState.value.schedule[Pair(day, period)]
        val dayName = when (day) {
            1 -> "Thứ 2"
            2 -> "Thứ 3"
            3 -> "Thứ 4"
            4 -> "Thứ 5"
            5 -> "Thứ 6"
            6 -> "Thứ 7"
            else -> ""
        }
        val roomStr = if (!cell?.room.isNullOrBlank()) " (${cell?.room})" else ""
        val info = if (cell?.subjectName != null) {
            "$dayName - Tiết $period: ${cell.subjectName}$roomStr\nGiáo viên: ${cell.teacher}"
        } else {
            "$dayName - Tiết $period: Tiết trống"
        }
        _uiState.update { it.copy(selectedCell = cell, selectedCellInfo = info) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(selectedCellInfo = null) }
    }
}
