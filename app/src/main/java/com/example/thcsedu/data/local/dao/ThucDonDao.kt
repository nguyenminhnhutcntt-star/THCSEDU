package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.ThucDonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ThucDonDao {
    @Query("SELECT * FROM thuc_don ORDER BY id ASC")
    fun getAllThucDon(): Flow<List<ThucDonEntity>>

    @Query("SELECT * FROM thuc_don ORDER BY id ASC")
    suspend fun getAllThucDonList(): List<ThucDonEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThucDonList(list: List<ThucDonEntity>)
}
