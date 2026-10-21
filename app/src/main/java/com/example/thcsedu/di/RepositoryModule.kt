package com.example.thcsedu.di

import com.example.thcsedu.data.repository.BangDiemRepositoryImpl
import com.example.thcsedu.data.repository.DiemDanhRepositoryImpl
import com.example.thcsedu.data.repository.DonXinNghiRepositoryImpl
import com.example.thcsedu.data.repository.HoaDonHocPhiRepositoryImpl
import com.example.thcsedu.data.repository.LichSuSuaDiemRepositoryImpl
import com.example.thcsedu.data.repository.SoDauBaiRepositoryImpl
import com.example.thcsedu.data.repository.ThongBaoRepositoryImpl
import com.example.thcsedu.data.repository.TinNhanRepositoryImpl
import com.example.thcsedu.data.repository.ViPhamRepositoryImpl
import com.example.thcsedu.domain.repository.BangDiemRepository
import com.example.thcsedu.domain.repository.DiemDanhRepository
import com.example.thcsedu.domain.repository.DonXinNghiRepository
import com.example.thcsedu.domain.repository.HoaDonHocPhiRepository
import com.example.thcsedu.domain.repository.LichSuSuaDiemRepository
import com.example.thcsedu.domain.repository.SoDauBaiRepository
import com.example.thcsedu.domain.repository.ThongBaoRepository
import com.example.thcsedu.domain.repository.TinNhanRepository
import com.example.thcsedu.domain.repository.ViPhamRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBangDiemRepository(
        impl: BangDiemRepositoryImpl
    ): BangDiemRepository

    @Binds
    @Singleton
    abstract fun bindDiemDanhRepository(
        impl: DiemDanhRepositoryImpl
    ): DiemDanhRepository

    @Binds
    @Singleton
    abstract fun bindSoDauBaiRepository(
        impl: SoDauBaiRepositoryImpl
    ): SoDauBaiRepository

    @Binds
    @Singleton
    abstract fun bindDonXinNghiRepository(
        impl: DonXinNghiRepositoryImpl
    ): DonXinNghiRepository

    @Binds
    @Singleton
    abstract fun bindLichSuSuaDiemRepository(
        impl: LichSuSuaDiemRepositoryImpl
    ): LichSuSuaDiemRepository

    @Binds
    @Singleton
    abstract fun bindViPhamRepository(
        impl: ViPhamRepositoryImpl
    ): ViPhamRepository

    @Binds
    @Singleton
    abstract fun bindHoaDonHocPhiRepository(
        impl: HoaDonHocPhiRepositoryImpl
    ): HoaDonHocPhiRepository

    @Binds
    @Singleton
    abstract fun bindThongBaoRepository(
        impl: ThongBaoRepositoryImpl
    ): ThongBaoRepository

    @Binds
    @Singleton
    abstract fun bindTinNhanRepository(
        impl: TinNhanRepositoryImpl
    ): TinNhanRepository
}
