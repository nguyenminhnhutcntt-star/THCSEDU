package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hoc_sinh")
data class HocSinhEntity(
    @PrimaryKey
    val maHocSinh: String,
    val hoTen: String,
    val ngaySinh: String,
    val gioiTinh: String,
    val maLop: String,
    val trangThai: String
)
