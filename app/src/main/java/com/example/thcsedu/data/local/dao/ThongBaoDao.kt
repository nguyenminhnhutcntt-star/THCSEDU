package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.ThongBaoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThongBaoDao {

    @Query("SELECT * FROM thong_bao ORDER BY id DESC")
    fun getAllThongBao(): Flow<List<ThongBaoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThongBao(thongBao: ThongBaoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThongBaoList(list: List<ThongBaoEntity>)
}
