package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.TaiKhoanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaiKhoanDao {

    @Query("SELECT * FROM tai_khoan WHERE tenDangNhap = :username LIMIT 1")
    suspend fun getTaiKhoanByUsername(username: String): TaiKhoanEntity?

    @Query("SELECT * FROM tai_khoan")
    fun getAllTaiKhoan(): Flow<List<TaiKhoanEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaiKhoan(taiKhoan: TaiKhoanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaiKhoanList(taiKhoanList: List<TaiKhoanEntity>)
}
