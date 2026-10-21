package com.example.thcsedu.ui.sodaubai

import com.example.thcsedu.data.local.entity.SoDauBaiEntity

data class SoDauBaiUiState(
    val maLop: String = "9A1",
    val ngay: String = "2026-10-13",
    val entries: List<SoDauBaiEntity> = emptyList(),
    val isLoading: Boolean = false,
    val selectedPeriod: Int? = null,
    val isEditing: Boolean = false,
    val errorMessage: String? = null
)
