package com.example.thcsedu.domain.repository

import com.example.thcsedu.data.local.entity.HoaDonHocPhiEntity
import kotlinx.coroutines.flow.Flow

interface HoaDonHocPhiRepository {
    fun getHoaDonByHocSinh(maHocSinh: String): Flow<List<HoaDonHocPhiEntity>>
    suspend fun saveHoaDon(hoaDon: HoaDonHocPhiEntity)
    suspend fun updatePaymentStatus(id: Long, daThanhToan: Boolean)
}
