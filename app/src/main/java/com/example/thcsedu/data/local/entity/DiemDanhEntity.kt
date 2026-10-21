package com.example.thcsedu.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "diem_danh",
    primaryKeys = ["maHocSinh", "ngay"]
)
data class DiemDanhEntity(
    val maHocSinh: String,
    val ngay: String, // format: "YYYY-MM-DD"
    val trangThai: String, // "CO_MAT", "VANG_PHEP", "VANG_KHONG_PHEP"
    val ghiChu: String? = null
)
