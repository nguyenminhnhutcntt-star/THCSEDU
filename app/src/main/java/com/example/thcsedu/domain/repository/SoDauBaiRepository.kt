package com.example.thcsedu.domain.repository

import com.example.thcsedu.data.local.entity.SoDauBaiEntity
import kotlinx.coroutines.flow.Flow

interface SoDauBaiRepository {
    fun getSoDauBaiByLopAndDate(maLop: String, ngay: String): Flow<List<SoDauBaiEntity>>
    suspend fun saveSoDauBai(soDauBai: SoDauBaiEntity)
}
