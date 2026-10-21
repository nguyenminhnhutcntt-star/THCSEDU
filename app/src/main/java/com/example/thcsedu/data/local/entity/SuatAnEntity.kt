package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suat_an")
data class SuatAnEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val maHocSinh: String,
    val ngay: String, // YYYY-MM-DD
    val trangThai: String, // "DA_DANG_KY", "DA_HUY"
    val giaSuatAn: Double = 35000.0
)
