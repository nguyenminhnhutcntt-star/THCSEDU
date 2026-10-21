package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.LopEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LopHocDao {

    @Query("SELECT * FROM lop_hoc")
    fun getAllLop(): Flow<List<LopEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateLopList(lopList: List<LopEntity>)
}
