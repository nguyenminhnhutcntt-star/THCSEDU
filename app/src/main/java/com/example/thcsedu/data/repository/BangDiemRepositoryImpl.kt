package com.example.thcsedu.data.repository

import com.example.thcsedu.data.local.dao.BangDiemDao
import com.example.thcsedu.data.local.dao.HocSinhDao
import com.example.thcsedu.data.local.entity.BangDiemEntity
import com.example.thcsedu.data.local.entity.HocSinhEntity
import com.example.thcsedu.data.remote.VnEduApiService
import com.example.thcsedu.data.remote.model.BangDiemDto
import com.example.thcsedu.data.remote.model.BangDiemSyncRequest
import com.example.thcsedu.data.util.Resource
import com.example.thcsedu.data.util.networkBoundResource
import com.example.thcsedu.domain.repository.BangDiemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BangDiemRepositoryImpl @Inject constructor(
    private val bangDiemDao: BangDiemDao,
    private val hocSinhDao: HocSinhDao,
    private val apiService: VnEduApiService
) : BangDiemRepository {

    override fun getBangDiem(
        maLop: String,
        maMonHoc: String,
        hocKy: Int
    ): Flow<List<BangDiemEntity>> {
        return bangDiemDao.getBangDiemByLopMonHocKy(maLop, maMonHoc, hocKy)
    }

    override fun getHocSinhByLop(maLop: String): Flow<List<HocSinhEntity>> {
        return hocSinhDao.getHocSinhByLop(maLop)
    }

    override suspend fun saveBangDiemList(dsBangDiem: List<BangDiemEntity>) {
        bangDiemDao.insertOrUpdateBangDiemList(dsBangDiem)
    }

    override fun getBangDiemWithNetwork(
        classId: String,
        maMonHoc: String,
        hocKy: Int
    ): Flow<Resource<List<BangDiemEntity>>> {
        return networkBoundResource(
            query = { bangDiemDao.getBangDiemByLopMonHocKy(classId, maMonHoc, hocKy) },
            fetch = { apiService.getBangDiemByClass(classId) },
            saveFetchResult = { remoteDtos ->
                val entities = remoteDtos
                    .filter { it.maMonHoc == maMonHoc && it.hocKy == hocKy }
                    .map { dto ->
                        BangDiemEntity(
                            maHocSinh = dto.maHocSinh,
                            maMonHoc = dto.maMonHoc,
                            hocKy = dto.hocKy,
                            namHoc = dto.namHoc,
                            dsDiemTX = dto.dsDiemTX,
                            diemGiuaKy = dto.diemGiuaKy,
                            diemCuoiKy = dto.diemCuoiKy,
                            nhanXet = dto.nhanXet
                        )
                    }
                bangDiemDao.insertOrUpdateBangDiemList(entities)
            },
            shouldFetch = { _ -> true }
        )
    }

    override suspend fun syncLocalChanges(): Result<Unit> {
        return try {
            val localList = bangDiemDao.getAllBangDiem()
            val dtos = localList.map { entity ->
                BangDiemDto(
                    maHocSinh = entity.maHocSinh,
                    maMonHoc = entity.maMonHoc,
                    hocKy = entity.hocKy,
                    namHoc = entity.namHoc,
                    dsDiemTX = entity.dsDiemTX,
                    diemGiuaKy = entity.diemGiuaKy,
                    diemCuoiKy = entity.diemCuoiKy,
                    nhanXet = entity.nhanXet
                )
            }
            val request = BangDiemSyncRequest(changes = dtos)
            val response = apiService.syncBangDiem(request)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Sync failed with code: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
