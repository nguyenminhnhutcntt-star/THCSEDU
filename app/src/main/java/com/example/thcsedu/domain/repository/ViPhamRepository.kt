package com.example.thcsedu.domain.repository

import com.example.thcsedu.data.local.entity.ViPhamEntity
import kotlinx.coroutines.flow.Flow

interface ViPhamRepository {
    fun getViPhamByLop(maLop: String): Flow<List<ViPhamEntity>>
    fun getViPhamByHocSinh(maHocSinh: String): Flow<List<ViPhamEntity>>
    suspend fun saveViPham(viPham: ViPhamEntity)
}
