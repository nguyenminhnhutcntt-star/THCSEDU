package com.example.thcsedu.ui.diemdanh

enum class TrangThaiDiemDanh(val value: String, val displayName: String) {
    CO_MAT("CO_MAT", "Có mặt"),
    VANG_PHEP("VANG_PHEP", "Vắng phép"),
    VANG_KHONG_PHEP("VANG_KHONG_PHEP", "Vắng không phép")
}

data class StudentAttendanceItem(
    val maHocSinh: String,
    val hoTen: String,
    val trangThai: TrangThaiDiemDanh = TrangThaiDiemDanh.CO_MAT,
    val ghiChu: String? = null
)

data class DiemDanhUiState(
    val selectedDate: String = "2026-10-13",
    val maLop: String = "LOP_01",
    val students: List<StudentAttendanceItem> = emptyList(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
