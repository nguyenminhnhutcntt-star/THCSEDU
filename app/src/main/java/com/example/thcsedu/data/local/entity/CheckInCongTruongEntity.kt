package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_in_cong_truong")
data class CheckInCongTruongEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val maHocSinh: String,
    val hoTen: String,
    val maLop: String,
    val thoiGianVao: String, // "dd/MM/yyyy HH:mm:ss"
    val trangThai: String // "Đúng giờ" / "Đi muộn"
)
