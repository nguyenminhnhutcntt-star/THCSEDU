package com.example.thcsedu.domain.repository

import com.example.thcsedu.data.local.entity.DiemDanhEntity
import com.example.thcsedu.data.local.entity.HocSinhEntity
import kotlinx.coroutines.flow.Flow

interface DiemDanhRepository {
    fun getHocSinhByLop(maLop: String): Flow<List<HocSinhEntity>>
    fun getDiemDanhByLopAndDate(maLop: String, ngay: String): Flow<List<DiemDanhEntity>>
    suspend fun saveDiemDanhList(diemDanhList: List<DiemDanhEntity>)
}
