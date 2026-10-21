package com.example.thcsedu.data.repository

import com.example.thcsedu.data.local.dao.DiemDanhDao
import com.example.thcsedu.data.local.dao.HocSinhDao
import com.example.thcsedu.data.local.entity.DiemDanhEntity
import com.example.thcsedu.data.local.entity.HocSinhEntity
import com.example.thcsedu.domain.repository.DiemDanhRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DiemDanhRepositoryImpl @Inject constructor(
    private val hocSinhDao: HocSinhDao,
    private val diemDanhDao: DiemDanhDao
) : DiemDanhRepository {

    override fun getHocSinhByLop(maLop: String): Flow<List<HocSinhEntity>> {
        return hocSinhDao.getHocSinhByLop(maLop)
    }

    override fun getDiemDanhByLopAndDate(maLop: String, ngay: String): Flow<List<DiemDanhEntity>> {
        return diemDanhDao.getDiemDanhByLopAndDate(maLop, ngay)
    }

    override suspend fun saveDiemDanhList(diemDanhList: List<DiemDanhEntity>) {
        diemDanhDao.insertOrUpdateDiemDanhList(diemDanhList)
    }
}
