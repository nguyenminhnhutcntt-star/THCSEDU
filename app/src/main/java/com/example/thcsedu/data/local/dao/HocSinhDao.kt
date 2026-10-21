package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.HocSinhEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HocSinhDao {

    @Query("SELECT * FROM hoc_sinh WHERE maLop = :maLop")
    fun getHocSinhByLop(maLop: String): Flow<List<HocSinhEntity>>

    @Query("SELECT * FROM hoc_sinh WHERE maHocSinh = :maHocSinh")
    fun getHocSinhById(maHocSinh: String): Flow<HocSinhEntity?>

    @Query("SELECT * FROM hoc_sinh WHERE maHocSinh = :maHocSinh")
    suspend fun getHocSinhByIdOnce(maHocSinh: String): HocSinhEntity?

    @Query("SELECT * FROM hoc_sinh")
    fun getAllHocSinh(): Flow<List<HocSinhEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHocSinh(hocSinh: HocSinhEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateHocSinhList(hocSinhList: List<HocSinhEntity>)

    @Delete
    suspend fun deleteHocSinh(hocSinh: HocSinhEntity)
}
