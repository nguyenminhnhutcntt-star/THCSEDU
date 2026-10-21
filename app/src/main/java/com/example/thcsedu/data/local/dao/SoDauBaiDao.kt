package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.SoDauBaiEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SoDauBaiDao {

    @Query("""
        SELECT * FROM so_dau_bai 
        WHERE maLop = :maLop AND ngay = :ngay 
        ORDER BY tietHoc ASC
    """)
    fun getSoDauBaiByLopAndDate(maLop: String, ngay: String): Flow<List<SoDauBaiEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSoDauBai(soDauBai: SoDauBaiEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSoDauBaiList(list: List<SoDauBaiEntity>)
}
