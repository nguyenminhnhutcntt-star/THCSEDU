package com.example.thcsedu.ui.timetable

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TimetableCell(
    val subjectName: String?,
    val teacher: String?
)

data class TimetableUiState(
    val maLop: String = "LOP_01",
    val schedule: Map<Pair<Int, Int>, TimetableCell> = mapOf(
        Pair(1, 1) to TimetableCell("Toán", "NV.A"),
        Pair(1, 2) to TimetableCell("Toán", "NV.A"),
        Pair(1, 3) to TimetableCell("Ngữ Văn", "TT.B"),
        Pair(1, 4) to TimetableCell("Tiếng Anh", "LC.C"),
        Pair(1, 5) to TimetableCell("Lịch Sử", "PT.D"),
        
        Pair(2, 1) to TimetableCell("Vật Lý", "TM.E"),
        Pair(2, 2) to TimetableCell("Hóa Học", "QN.F"),
        Pair(2, 3) to TimetableCell("Sinh Học", "LH.G"),
        Pair(2, 4) to TimetableCell("Toán", "NV.A"),
        Pair(2, 5) to TimetableCell("Thể Dục", "VD.H"),
        
        Pair(3, 1) to TimetableCell("Ngữ Văn", "TT.B"),
        Pair(3, 2) to TimetableCell("Ngữ Văn", "TT.B"),
        Pair(3, 3) to TimetableCell("Tiếng Anh", "LC.C"),
        Pair(3, 4) to TimetableCell("Địa Lý", "NK.I"),
        Pair(3, 5) to TimetableCell("Công Nghệ", "HT.K"),
        
        Pair(4, 1) to TimetableCell("Toán", "NV.A"),
        Pair(4, 2) to TimetableCell("Vật Lý", "TM.E"),
        Pair(4, 3) to TimetableCell("Hóa Học", "QN.F"),
        Pair(4, 4) to TimetableCell("GDCD", "PT.L"),
        Pair(4, 5) to TimetableCell(null, null),
        
        Pair(5, 1) to TimetableCell("Tiếng Anh", "LC.C"),
        Pair(5, 2) to TimetableCell("Tiếng Anh", "LC.C"),
        Pair(5, 3) to TimetableCell("Ngữ Văn", "TT.B"),
        Pair(5, 4) to TimetableCell("Sinh Học", "LH.G"),
        Pair(5, 5) to TimetableCell("Mỹ Thuật", "TT.M"),
        
        Pair(6, 1) to TimetableCell("Lịch Sử", "PT.D"),
        Pair(6, 2) to TimetableCell("Địa Lý", "NK.I"),
        Pair(6, 3) to TimetableCell("Thể Dục", "VD.H"),
        Pair(6, 4) to TimetableCell(null, null),
        Pair(6, 5) to TimetableCell(null, null)
    ),
    val selectedCell: TimetableCell? = null,
    val selectedCellInfo: String? = null
)

@HiltViewModel
class TimetableViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TimetableUiState())
    val uiState: StateFlow<TimetableUiState> = _uiState.asStateFlow()

    fun onCellClicked(day: Int, period: Int) {
        val cell = uiState.value.schedule[Pair(day, period)]
        val dayName = when(day) {
            1 -> "Thứ 2"
            2 -> "Thứ 3"
            3 -> "Thứ 4"
            4 -> "Thứ 5"
            5 -> "Thứ 6"
            6 -> "Thứ 7"
            else -> ""
        }
        val info = if (cell?.subjectName != null) {
            "$dayName - Tiết $period: ${cell.subjectName} (GV: ${cell.teacher})"
        } else {
            "$dayName - Tiết $period: Tiết trống"
        }
        _uiState.update { it.copy(selectedCell = cell, selectedCellInfo = info) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(selectedCellInfo = null) }
    }
}
