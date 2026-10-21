package com.example.thcsedu.domain.repository

import com.example.thcsedu.data.local.entity.DonXinNghiEntity
import kotlinx.coroutines.flow.Flow

interface DonXinNghiRepository {
    fun getAllDonXinNghi(): Flow<List<DonXinNghiEntity>>
    fun getDonXinNghiByHocSinh(maHocSinh: String): Flow<List<DonXinNghiEntity>>
    suspend fun saveDon(don: DonXinNghiEntity)
    suspend fun updateStatus(id: Long, trangThai: String)
}
