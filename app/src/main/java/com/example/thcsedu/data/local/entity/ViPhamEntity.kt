package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vi_pham")
data class ViPhamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val maHocSinh: String,
    val maLop: String,
    val ngayViPham: String, // "YYYY-MM-DD"
    val tenLoi: String, // e.g., "Đi trễ", "Không đồng phục", "Nói chuyện riêng"
    val diemTru: Int // e.g., 2, 5, 10
)
