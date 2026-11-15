package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.PhanCongGiaoVienEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhanCongGiaoVienDao {

    @Query("SELECT * FROM phan_cong_giao_vien ORDER BY maLop ASC, maMonHoc ASC")
    fun getAllPhanCong(): Flow<List<PhanCongGiaoVienEntity>>

    @Query("SELECT * FROM phan_cong_giao_vien WHERE maLop = :maLop")
    fun getPhanCongByLop(maLop: String): Flow<List<PhanCongGiaoVienEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhanCong(phanCong: PhanCongGiaoVienEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhanCongList(list: List<PhanCongGiaoVienEntity>)

    @Delete
    suspend fun deletePhanCong(phanCong: PhanCongGiaoVienEntity)

    @Query("DELETE FROM phan_cong_giao_vien WHERE id = :id")
    suspend fun deletePhanCongById(id: Long)
}
