package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lop_hoc")
data class LopEntity(
    @PrimaryKey
    val maLop: String,
    val tenLop: String,
    val nienKhoa: String
)
