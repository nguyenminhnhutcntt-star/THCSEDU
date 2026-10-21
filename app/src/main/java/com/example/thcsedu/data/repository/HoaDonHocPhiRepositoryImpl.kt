package com.example.thcsedu.data.repository

import com.example.thcsedu.data.local.dao.HoaDonHocPhiDao
import com.example.thcsedu.data.local.entity.HoaDonHocPhiEntity
import com.example.thcsedu.domain.repository.HoaDonHocPhiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HoaDonHocPhiRepositoryImpl @Inject constructor(
    private val hoaDonHocPhiDao: HoaDonHocPhiDao
) : HoaDonHocPhiRepository {

    override fun getHoaDonByHocSinh(maHocSinh: String): Flow<List<HoaDonHocPhiEntity>> {
        return hoaDonHocPhiDao.getHoaDonByHocSinh(maHocSinh)
    }

    override suspend fun saveHoaDon(hoaDon: HoaDonHocPhiEntity) {
        hoaDonHocPhiDao.insertHoaDon(hoaDon)
    }

    override suspend fun updatePaymentStatus(id: Long, daThanhToan: Boolean) {
        hoaDonHocPhiDao.updatePaymentStatus(id, daThanhToan)
    }
}
