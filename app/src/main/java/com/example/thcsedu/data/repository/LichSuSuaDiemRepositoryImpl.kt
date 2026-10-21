package com.example.thcsedu.data.repository

import com.example.thcsedu.data.local.dao.LichSuSuaDiemDao
import com.example.thcsedu.data.local.entity.LichSuSuaDiemEntity
import com.example.thcsedu.domain.repository.LichSuSuaDiemRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LichSuSuaDiemRepositoryImpl @Inject constructor(
    private val lichSuSuaDiemDao: LichSuSuaDiemDao
) : LichSuSuaDiemRepository {

    override fun getHistory(maHocSinh: String, maMonHoc: String): Flow<List<LichSuSuaDiemEntity>> {
        return lichSuSuaDiemDao.getHistoryByStudentAndSubject(maHocSinh, maMonHoc)
    }

    override suspend fun logGradeChange(log: LichSuSuaDiemEntity) {
        lichSuSuaDiemDao.insertAuditLog(log)
    }
}
