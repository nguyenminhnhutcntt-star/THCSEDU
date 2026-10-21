package com.example.thcsedu.domain.repository

import com.example.thcsedu.data.local.entity.LichSuSuaDiemEntity
import kotlinx.coroutines.flow.Flow

interface LichSuSuaDiemRepository {
    fun getHistory(maHocSinh: String, maMonHoc: String): Flow<List<LichSuSuaDiemEntity>>
    suspend fun logGradeChange(log: LichSuSuaDiemEntity)
}
