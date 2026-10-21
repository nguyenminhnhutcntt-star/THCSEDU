package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.SuatAnEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SuatAnDao {
    @Query("SELECT * FROM suat_an WHERE maHocSinh = :maHocSinh ORDER BY ngay ASC")
    fun getSuatAnByStudent(maHocSinh: String): Flow<List<SuatAnEntity>>

    @Query("SELECT * FROM suat_an WHERE maHocSinh = :maHocSinh ORDER BY ngay ASC")
    suspend fun getSuatAnListByStudent(maHocSinh: String): List<SuatAnEntity>

    @Query("SELECT * FROM suat_an WHERE maHocSinh = :maHocSinh AND ngay = :ngay")
    suspend fun getSuatAnByDate(maHocSinh: String, ngay: String): SuatAnEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuatAn(suatAn: SuatAnEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuatAnList(list: List<SuatAnEntity>)

    @Query("UPDATE suat_an SET trangThai = :trangThai WHERE maHocSinh = :maHocSinh AND ngay BETWEEN :tuNgay AND :denNgay")
    suspend fun updateTrangThaiByDateRange(maHocSinh: String, tuNgay: String, denNgay: String, trangThai: String)

    @Query("UPDATE suat_an SET trangThai = :trangThai WHERE maHocSinh = :maHocSinh AND ngay = :ngay")
    suspend fun updateTrangThaiByDate(maHocSinh: String, ngay: String, trangThai: String)

    @Query("SELECT SUM(giaSuatAn) FROM suat_an WHERE maHocSinh = :maHocSinh AND trangThai = 'DA_HUY' AND ngay LIKE :thangPattern")
    suspend fun getTongTienHoanThang(maHocSinh: String, thangPattern: String): Double?

    @Query("SELECT COUNT(*) FROM suat_an WHERE maHocSinh = :maHocSinh AND trangThai = 'DA_HUY' AND ngay LIKE :thangPattern")
    suspend fun getSoBuaDaCatThang(maHocSinh: String, thangPattern: String): Int?
}
