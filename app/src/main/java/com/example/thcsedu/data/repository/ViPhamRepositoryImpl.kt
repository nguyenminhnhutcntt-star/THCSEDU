package com.example.thcsedu.data.repository

import com.example.thcsedu.data.local.dao.ViPhamDao
import com.example.thcsedu.data.local.entity.ViPhamEntity
import com.example.thcsedu.domain.repository.ViPhamRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ViPhamRepositoryImpl @Inject constructor(
    private val viPhamDao: ViPhamDao
) : ViPhamRepository {

    override fun getViPhamByLop(maLop: String): Flow<List<ViPhamEntity>> {
        return viPhamDao.getViPhamByLop(maLop)
    }

    override fun getViPhamByHocSinh(maHocSinh: String): Flow<List<ViPhamEntity>> {
        return viPhamDao.getViPhamByHocSinh(maHocSinh)
    }

    override suspend fun saveViPham(viPham: ViPhamEntity) {
        viPhamDao.insertViPham(viPham)
    }
}
