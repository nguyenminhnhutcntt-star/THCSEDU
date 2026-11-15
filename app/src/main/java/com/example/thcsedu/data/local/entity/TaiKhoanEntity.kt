package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tai_khoan")
data class TaiKhoanEntity(
    @PrimaryKey
    val tenDangNhap: String,
    val matKhauHash: String,
    val vaiTro: String, // "TEACHER", "PARENT", "ADMIN"
    val hoTen: String,
    val maDinhDanh: String? = null // e.g. "HS01" for Parent, "GV01" for Teacher
)
