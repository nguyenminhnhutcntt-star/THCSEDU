package com.example.thcsedu.domain.repository

import com.example.thcsedu.data.local.entity.ThongBaoEntity
import kotlinx.coroutines.flow.Flow

interface ThongBaoRepository {
    fun getAllThongBao(): Flow<List<ThongBaoEntity>>
    suspend fun saveThongBao(thongBao: ThongBaoEntity)
}
