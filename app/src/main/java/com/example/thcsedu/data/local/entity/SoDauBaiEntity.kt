package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "so_dau_bai")
data class SoDauBaiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val maLop: String,
    val ngay: String, // format: "YYYY-MM-DD"
    val tietHoc: Int, // 1 to 5
    val tenMon: String,
    val tenBaiDay: String,
    val nhanXet: String,
    val xepLoaiTiet: String, // "A", "B", "C", "D"
    val giaoVienId: String
)
