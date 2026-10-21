package com.example.thcsedu.data.repository

import com.example.thcsedu.data.local.dao.DonXinNghiDao
import com.example.thcsedu.data.local.entity.DonXinNghiEntity
import com.example.thcsedu.domain.repository.DonXinNghiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DonXinNghiRepositoryImpl @Inject constructor(
    private val donXinNghiDao: DonXinNghiDao
) : DonXinNghiRepository {

    override fun getAllDonXinNghi(): Flow<List<DonXinNghiEntity>> {
        return donXinNghiDao.getAllDonXinNghi()
    }

    override fun getDonXinNghiByHocSinh(maHocSinh: String): Flow<List<DonXinNghiEntity>> {
        return donXinNghiDao.getDonXinNghiByHocSinh(maHocSinh)
    }

    override suspend fun saveDon(don: DonXinNghiEntity) {
        donXinNghiDao.insertOrUpdateDon(don)
    }

    override suspend fun updateStatus(id: Long, trangThai: String) {
        donXinNghiDao.updateStatus(id, trangThai)
    }
}
