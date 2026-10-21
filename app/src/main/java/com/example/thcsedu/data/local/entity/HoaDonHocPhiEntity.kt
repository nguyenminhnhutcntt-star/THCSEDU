package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hoa_don_hoc_phi")
data class HoaDonHocPhiEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val maHocSinh: String,
    val thangNam: String, // e.g. "10/2026"
    val tenKhoanThu: String, // "Học phí", "Bán trú", "BHYT", "Quỹ lớp"
    val soTien: Double,
    val daThanhToan: Boolean,
    val hanNop: String // "2026-10-25"
)
