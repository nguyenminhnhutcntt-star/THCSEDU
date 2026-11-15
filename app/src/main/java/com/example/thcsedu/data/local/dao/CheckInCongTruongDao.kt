package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.CheckInCongTruongEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInCongTruongDao {

    @Query("SELECT * FROM check_in_cong_truong ORDER BY id DESC")
    fun getAllCheckIn(): Flow<List<CheckInCongTruongEntity>>

    @Query("SELECT * FROM check_in_cong_truong ORDER BY id DESC LIMIT :limit")
    fun getRecentCheckIns(limit: Int = 30): Flow<List<CheckInCongTruongEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(item: CheckInCongTruongEntity)
}
