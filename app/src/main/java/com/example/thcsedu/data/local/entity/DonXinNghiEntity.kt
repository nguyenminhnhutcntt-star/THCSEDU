package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "don_xin_nghi")
data class DonXinNghiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val maHocSinh: String,
    val tuNgay: String, // "YYYY-MM-DD"
    val denNgay: String, // "YYYY-MM-DD"
    val lyDo: String,
    val trangThai: String // "CHO_DUYET", "DA_DUYET", "TU_CHOI"
)
