package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.HoaDonHocPhiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HoaDonHocPhiDao {

    @Query("SELECT * FROM hoa_don_hoc_phi WHERE maHocSinh = :maHocSinh ORDER BY id DESC")
    fun getHoaDonByHocSinh(maHocSinh: String): Flow<List<HoaDonHocPhiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoaDon(hoaDon: HoaDonHocPhiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHoaDonList(list: List<HoaDonHocPhiEntity>)

    @Query("UPDATE hoa_don_hoc_phi SET daThanhToan = :daThanhToan WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, daThanhToan: Boolean)
}
