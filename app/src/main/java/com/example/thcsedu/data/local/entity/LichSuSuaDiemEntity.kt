package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lich_su_sua_diem")
data class LichSuSuaDiemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val maHocSinh: String,
    val maMonHoc: String,
    val loaiDiem: String, // e.g. "ĐĐGtx", "Giữa kỳ", "Cuối kỳ"
    val diemCu: Float?,
    val diemMoi: Float,
    val lyDoSua: String,
    val nguoiSua: String, // e.g. "Giáo viên"
    val thoiGian: Long = System.currentTimeMillis()
)
