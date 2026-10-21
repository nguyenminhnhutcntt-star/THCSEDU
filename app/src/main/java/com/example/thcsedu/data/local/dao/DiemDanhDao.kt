package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.DiemDanhEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DiemDanhDao {

    @Query("""
        SELECT diem_danh.* FROM diem_danh 
        INNER JOIN hoc_sinh ON diem_danh.maHocSinh = hoc_sinh.maHocSinh 
        WHERE hoc_sinh.maLop = :maLop AND diem_danh.ngay = :ngay
    """)
    fun getDiemDanhByLopAndDate(maLop: String, ngay: String): Flow<List<DiemDanhEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDiemDanhList(diemDanhList: List<DiemDanhEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDiemDanh(diemDanh: DiemDanhEntity)

    @Query("SELECT * FROM diem_danh WHERE ngay = :ngay")
    suspend fun getDiemDanhByDateSync(ngay: String): List<DiemDanhEntity>
}
