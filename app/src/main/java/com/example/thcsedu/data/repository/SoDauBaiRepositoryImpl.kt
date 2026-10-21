package com.example.thcsedu.data.repository

import com.example.thcsedu.data.local.dao.SoDauBaiDao
import com.example.thcsedu.data.local.entity.SoDauBaiEntity
import com.example.thcsedu.domain.repository.SoDauBaiRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SoDauBaiRepositoryImpl @Inject constructor(
    private val soDauBaiDao: SoDauBaiDao
) : SoDauBaiRepository {

    override fun getSoDauBaiByLopAndDate(maLop: String, ngay: String): Flow<List<SoDauBaiEntity>> {
        return soDauBaiDao.getSoDauBaiByLopAndDate(maLop, ngay)
    }

    override suspend fun saveSoDauBai(soDauBai: SoDauBaiEntity) {
        soDauBaiDao.insertOrUpdateSoDauBai(soDauBai)
    }
}
