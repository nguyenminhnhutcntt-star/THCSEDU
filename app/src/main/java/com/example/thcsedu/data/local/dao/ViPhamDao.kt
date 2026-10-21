package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.ViPhamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ViPhamDao {

    @Query("SELECT * FROM vi_pham WHERE maLop = :maLop ORDER BY id DESC")
    fun getViPhamByLop(maLop: String): Flow<List<ViPhamEntity>>

    @Query("SELECT * FROM vi_pham WHERE maHocSinh = :maHocSinh ORDER BY id DESC")
    fun getViPhamByHocSinh(maHocSinh: String): Flow<List<ViPhamEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertViPham(viPham: ViPhamEntity)

    @Delete
    suspend fun deleteViPham(viPham: ViPhamEntity)
}
