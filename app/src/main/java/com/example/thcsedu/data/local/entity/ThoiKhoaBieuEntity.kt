package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thoi_khoa_bieu")
data class ThoiKhoaBieuEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val maLop: String,
    val thu: Int, // 2 = Thứ 2, 3 = Thứ 3, ..., 7 = Thứ 7
    val tiet: Int, // 1 to 5
    val maMonHoc: String,
    val tenMonHoc: String,
    val giaoVien: String,
    val phongHoc: String = ""
)
