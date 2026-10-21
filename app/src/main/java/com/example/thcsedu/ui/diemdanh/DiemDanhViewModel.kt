package com.example.thcsedu.ui.diemdanh

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.thcsedu.data.local.entity.DiemDanhEntity
import com.example.thcsedu.domain.repository.DiemDanhRepository
import com.example.thcsedu.domain.repository.DonXinNghiRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiemDanhViewModel @Inject constructor(
    private val repository: DiemDanhRepository,
    private val donXinNghiRepository: DonXinNghiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiemDanhUiState())
    val uiState: StateFlow<DiemDanhUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun loadAttendance(maLop: String, date: String) {
        _uiState.update { it.copy(maLop = maLop, selectedDate = date, isLoading = true, errorMessage = null) }
        loadJob?.cancel()

        loadJob = viewModelScope.launch {
            combine(
                repository.getHocSinhByLop(maLop),
                repository.getDiemDanhByLopAndDate(maLop, date),
                donXinNghiRepository.getAllDonXinNghi()
            ) { students, attendanceList, leaveRequests ->
                val attendanceMap = attendanceList.associateBy { it.maHocSinh }
                
                // Find approved leave requests covering `date` (tuNgay <= date <= denNgay)
                val excusedStudents = leaveRequests
                    .filter { it.trangThai == "DA_DUYET" && it.tuNgay <= date && it.denNgay >= date }
                    .map { it.maHocSinh }
                    .toSet()

                students.map { student ->
                    val existing = attendanceMap[student.maHocSinh]
                    val status = when {
                        existing != null -> {
                            TrangThaiDiemDanh.entries.find { it.value == existing.trangThai } ?: TrangThaiDiemDanh.CO_MAT
                        }
                        student.maHocSinh in excusedStudents -> {
                            TrangThaiDiemDanh.VANG_PHEP
                        }
                        else -> {
                            TrangThaiDiemDanh.CO_MAT
                        }
                    }

                    StudentAttendanceItem(
                        maHocSinh = student.maHocSinh,
                        hoTen = student.hoTen,
                        trangThai = status,
                        ghiChu = existing?.ghiChu ?: if (student.maHocSinh in excusedStudents) "Đơn xin nghỉ đã duyệt" else null
                    )
                }
            }.catch { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Failed to load attendance"
                    )
                }
            }.collect { studentItems ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        students = studentItems
                    )
                }
            }
        }
    }

    fun updateDate(date: String) {
        val currentState = _uiState.value
        loadAttendance(currentState.maLop, date)
    }

    fun updateStudentStatus(maHocSinh: String, status: TrangThaiDiemDanh) {
        _uiState.update { currentState ->
            val updatedStudents = currentState.students.map { student ->
                if (student.maHocSinh == maHocSinh) {
                    student.copy(trangThai = status)
                } else {
                    student
                }
            }
            currentState.copy(students = updatedStudents)
        }
    }

    fun submitBatchAttendance(onSuccess: () -> Unit = {}) {
        val currentState = _uiState.value
        val entities = currentState.students.map { student ->
            DiemDanhEntity(
                maHocSinh = student.maHocSinh,
                ngay = currentState.selectedDate,
                trangThai = student.trangThai.value,
                ghiChu = student.ghiChu
            )
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            try {
                repository.saveDiemDanhList(entities)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        successMessage = "Attendance saved successfully!"
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = e.localizedMessage ?: "Failed to save attendance"
                    )
                }
            }
        }
    }
}
