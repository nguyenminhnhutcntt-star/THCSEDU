package com.example.thcsedu.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.thcsedu.data.local.entity.BangDiemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BangDiemDao {

    @Query("""
        SELECT bang_diem.* FROM bang_diem 
        INNER JOIN hoc_sinh ON bang_diem.maHocSinh = hoc_sinh.maHocSinh 
        WHERE hoc_sinh.maLop = :maLop 
          AND bang_diem.maMonHoc = :maMonHoc 
          AND bang_diem.hocKy = :hocKy
    """)
    fun getBangDiemByLopMonHocKy(
        maLop: String,
        maMonHoc: String,
        hocKy: Int
    ): Flow<List<BangDiemEntity>>

    @Query("""
        SELECT bang_diem.* FROM bang_diem 
        INNER JOIN hoc_sinh ON bang_diem.maHocSinh = hoc_sinh.maHocSinh 
        WHERE hoc_sinh.maLop = :maLop 
          AND bang_diem.maMonHoc = :maMonHoc 
          AND bang_diem.hocKy = :hocKy 
          AND bang_diem.namHoc = :namHoc
    """)
    fun getBangDiemByLopMonHocKyNamHoc(
        maLop: String,
        maMonHoc: String,
        hocKy: Int,
        namHoc: String
    ): Flow<List<BangDiemEntity>>

    @Query("SELECT * FROM bang_diem WHERE maHocSinh = :maHocSinh")
    fun getBangDiemByHocSinh(maHocSinh: String): Flow<List<BangDiemEntity>>

    @Query("SELECT * FROM bang_diem WHERE maHocSinh = :maHocSinh AND namHoc = :namHoc")
    fun getBangDiemByHocSinhAndNamHoc(maHocSinh: String, namHoc: String): Flow<List<BangDiemEntity>>

    @Query("""
        SELECT * FROM bang_diem 
        WHERE maHocSinh = :maHocSinh 
          AND maMonHoc = :maMonHoc 
          AND hocKy = :hocKy 
          AND namHoc = :namHoc 
        LIMIT 1
    """)
    fun getBangDiemDetail(
        maHocSinh: String,
        maMonHoc: String,
        hocKy: Int,
        namHoc: String
    ): Flow<BangDiemEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBangDiem(bangDiem: BangDiemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateBangDiemList(bangDiemList: List<BangDiemEntity>)

    @Query("SELECT * FROM bang_diem")
    suspend fun getAllBangDiem(): List<BangDiemEntity>

    @Delete
    suspend fun deleteBangDiem(bangDiem: BangDiemEntity)
}
