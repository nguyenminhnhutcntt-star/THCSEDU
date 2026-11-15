package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.ThoiKhoaBieuEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThoiKhoaBieuDao {

    @Query("SELECT * FROM thoi_khoa_bieu WHERE maLop = :maLop ORDER BY thu ASC, tiet ASC")
    fun getThoiKhoaBieuByLop(maLop: String): Flow<List<ThoiKhoaBieuEntity>>

    @Query("SELECT * FROM thoi_khoa_bieu WHERE maLop = :maLop ORDER BY thu ASC, tiet ASC")
    suspend fun getThoiKhoaBieuByLopOnce(maLop: String): List<ThoiKhoaBieuEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThoiKhoaBieuList(list: List<ThoiKhoaBieuEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateThoiKhoaBieu(item: ThoiKhoaBieuEntity)

    @Query("DELETE FROM thoi_khoa_bieu WHERE maLop = :maLop")
    suspend fun deleteThoiKhoaBieuByLop(maLop: String)
}
