package com.example.thcsedu.data.repository

import com.example.thcsedu.data.local.dao.ThongBaoDao
import com.example.thcsedu.data.local.entity.ThongBaoEntity
import com.example.thcsedu.domain.repository.ThongBaoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ThongBaoRepositoryImpl @Inject constructor(
    private val thongBaoDao: ThongBaoDao
) : ThongBaoRepository {

    override fun getAllThongBao(): Flow<List<ThongBaoEntity>> {
        return thongBaoDao.getAllThongBao()
    }

    override suspend fun saveThongBao(thongBao: ThongBaoEntity) {
        thongBaoDao.insertThongBao(thongBao)
    }
}
