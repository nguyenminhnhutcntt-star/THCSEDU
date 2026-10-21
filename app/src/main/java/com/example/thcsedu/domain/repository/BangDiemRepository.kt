package com.example.thcsedu.domain.repository

import com.example.thcsedu.data.local.entity.BangDiemEntity
import com.example.thcsedu.data.local.entity.HocSinhEntity
import com.example.thcsedu.data.util.Resource
import kotlinx.coroutines.flow.Flow

interface BangDiemRepository {
    fun getBangDiem(maLop: String, maMonHoc: String, hocKy: Int): Flow<List<BangDiemEntity>>
    fun getHocSinhByLop(maLop: String): Flow<List<HocSinhEntity>>
    suspend fun saveBangDiemList(dsBangDiem: List<BangDiemEntity>)

    // Offline-First methods requested
    fun getBangDiemWithNetwork(
        classId: String,
        maMonHoc: String,
        hocKy: Int
    ): Flow<Resource<List<BangDiemEntity>>>

    suspend fun syncLocalChanges(): Result<Unit>
}
