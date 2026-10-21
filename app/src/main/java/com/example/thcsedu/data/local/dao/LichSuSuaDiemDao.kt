package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.LichSuSuaDiemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LichSuSuaDiemDao {

    @Query("SELECT * FROM lich_su_sua_diem WHERE maHocSinh = :maHocSinh AND maMonHoc = :maMonHoc ORDER BY thoiGian DESC")
    fun getHistoryByStudentAndSubject(maHocSinh: String, maMonHoc: String): Flow<List<LichSuSuaDiemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: LichSuSuaDiemEntity)
}
