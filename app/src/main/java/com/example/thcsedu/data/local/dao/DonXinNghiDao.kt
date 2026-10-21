package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.DonXinNghiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DonXinNghiDao {

    @Query("SELECT * FROM don_xin_nghi ORDER BY id DESC")
    fun getAllDonXinNghi(): Flow<List<DonXinNghiEntity>>

    @Query("SELECT * FROM don_xin_nghi WHERE id = :id")
    suspend fun getDonById(id: Long): DonXinNghiEntity?

    @Query("SELECT * FROM don_xin_nghi WHERE maHocSinh = :maHocSinh ORDER BY id DESC")
    fun getDonXinNghiByHocSinh(maHocSinh: String): Flow<List<DonXinNghiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateDon(don: DonXinNghiEntity)

    @Query("UPDATE don_xin_nghi SET trangThai = :trangThai WHERE id = :id")
    suspend fun updateStatus(id: Long, trangThai: String)
}
