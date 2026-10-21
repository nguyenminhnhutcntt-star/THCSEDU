package com.example.thcsedu.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "bang_diem",
    primaryKeys = ["maHocSinh", "maMonHoc", "hocKy", "namHoc"]
)
data class BangDiemEntity(
    val maHocSinh: String,
    val maMonHoc: String,
    val hocKy: Int,
    val namHoc: String,
    val dsDiemTX: List<Float>,
    val diemGiuaKy: Float?,
    val diemCuoiKy: Float?,
    val nhanXet: String?
)
