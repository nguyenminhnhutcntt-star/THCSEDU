package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "thong_bao")
data class ThongBaoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val tieuDe: String,
    val noiDung: String,
    val ngayGui: String, // "YYYY-MM-DD"
    val nguoiGui: String // "Ban Giám Hiệu" / "Giáo viên chủ nhiệm"
)
