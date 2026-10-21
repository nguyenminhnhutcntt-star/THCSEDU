package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mon_hoc")
data class MonHocEntity(
    @PrimaryKey
    val maMonHoc: String,
    val tenMonHoc: String,
    val loaiDanhGia: LoaiDanhGia
)
