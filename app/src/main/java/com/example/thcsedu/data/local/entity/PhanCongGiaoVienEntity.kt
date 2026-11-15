package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "phan_cong_giao_vien")
data class PhanCongGiaoVienEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val maGiaoVien: String,
    val tenGiaoVien: String,
    val maLop: String,
    val maMonHoc: String,
    val tenMonHoc: String,
    val vaiTro: String = "Bộ môn" // "Chủ nhiệm" / "Bộ môn"
)
