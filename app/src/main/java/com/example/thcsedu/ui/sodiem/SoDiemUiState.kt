package com.example.thcsedu.ui.sodiem

data class PendingGradeChange(
    val maHocSinh: String,
    val loaiDiem: String,
    val diemCu: Float?,
    val diemMoi: Float,
    val onConfirm: (String) -> Unit
)

data class HocSinhDiemItemUiState(
    val maHocSinh: String,
    val hoTen: String,
    val dsDiemTX: List<Float> = emptyList(),
    val diemGiuaKy: Float? = null,
    val diemCuoiKy: Float? = null,
    val diemTBMon: Float? = null,
    val nhanXet: String? = null
)

data class SoDiemUiState(
    val isLoading: Boolean = false,
    val dsHocSinhDiem: List<HocSinhDiemItemUiState> = emptyList(),
    val errorMessage: String? = null,
    val currentMaMonHoc: String = "",
    val currentHocKy: Int = 1,
    val currentNamHoc: String = "2024-2025",
    val isGradeLocked: Boolean = false,
    val pendingGradeChange: PendingGradeChange? = null
)
