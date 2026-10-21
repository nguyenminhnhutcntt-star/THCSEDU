package com.example.thcsedu.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.thcsedu.data.local.converter.Converters
import com.example.thcsedu.data.local.dao.*
import com.example.thcsedu.data.local.entity.*

@Database(
    entities = [
        HocSinhEntity::class,
        MonHocEntity::class,
        BangDiemEntity::class,
        DiemDanhEntity::class,
        LopEntity::class,
        SoDauBaiEntity::class,
        DonXinNghiEntity::class,
        LichSuSuaDiemEntity::class,
        ViPhamEntity::class,
        HoaDonHocPhiEntity::class,
        ThongBaoEntity::class,
        TinNhanEntity::class,
        ThucDonEntity::class,
        SuatAnEntity::class
    ],
    version = 10,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun bangDiemDao(): BangDiemDao
    abstract fun hocSinhDao(): HocSinhDao
    abstract fun monHocDao(): MonHocDao
    abstract fun diemDanhDao(): DiemDanhDao
    abstract fun lopHocDao(): LopHocDao
    abstract fun soDauBaiDao(): SoDauBaiDao
    abstract fun donXinNghiDao(): DonXinNghiDao
    abstract fun lichSuSuaDiemDao(): LichSuSuaDiemDao
    abstract fun viPhamDao(): ViPhamDao
    abstract fun hoaDonHocPhiDao(): HoaDonHocPhiDao
    abstract fun thongBaoDao(): ThongBaoDao
    abstract fun tinNhanDao(): TinNhanDao
    abstract fun thucDonDao(): ThucDonDao
    abstract fun suatAnDao(): SuatAnDao
}
