package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.MonHocEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonHocDao {

    @Query("SELECT * FROM mon_hoc")
    fun getAllMonHoc(): Flow<List<MonHocEntity>>

    @Query("SELECT * FROM mon_hoc WHERE maMonHoc = :maMonHoc")
    fun getMonHocById(maMonHoc: String): Flow<MonHocEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMonHoc(monHoc: MonHocEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMonHocList(monHocList: List<MonHocEntity>)

    @Delete
    suspend fun deleteMonHoc(monHoc: MonHocEntity)
}
