package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thuc_don")
data class ThucDonEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val ngay: String, // YYYY-MM-DD
    val thuInWeek: String, // "Thứ 2" .. "Thứ 6"
    val buaTrua: String,
    val buaPhu: String,
    val calo: Int
)
