package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.TinNhanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TinNhanDao {

    @Query("""
        SELECT * FROM tin_nhan 
        WHERE (senderId = :user1 AND receiverId = :user2) 
           OR (senderId = :user2 AND receiverId = :user1) 
        ORDER BY timestamp ASC
    """)
    fun getMessagesBetween(user1: String, user2: String): Flow<List<TinNhanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: TinNhanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageList(list: List<TinNhanEntity>)
}
