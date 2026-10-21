package com.example.thcsedu.di

import android.content.Context
import androidx.room.Room
import com.example.thcsedu.data.local.AppDatabase
import com.example.thcsedu.data.local.AppDatabaseCallback
import com.example.thcsedu.data.local.dao.*
import com.example.thcsedu.data.remote.VnEduApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        callbackProvider: Provider<AppDatabaseCallback>
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "edumanage.db"
        )
            .addCallback(callbackProvider.get())
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideBangDiemDao(database: AppDatabase): BangDiemDao {
        return database.bangDiemDao()
    }

    @Provides
    @Singleton
    fun provideHocSinhDao(database: AppDatabase): HocSinhDao {
        return database.hocSinhDao()
    }

    @Provides
    @Singleton
    fun provideMonHocDao(database: AppDatabase): MonHocDao {
        return database.monHocDao()
    }

    @Provides
    @Singleton
    fun provideDiemDanhDao(database: AppDatabase): DiemDanhDao {
        return database.diemDanhDao()
    }

    @Provides
    @Singleton
    fun provideLopHocDao(database: AppDatabase): LopHocDao {
        return database.lopHocDao()
    }

    @Provides
    @Singleton
    fun provideSoDauBaiDao(database: AppDatabase): SoDauBaiDao {
        return database.soDauBaiDao()
    }

    @Provides
    @Singleton
    fun provideDonXinNghiDao(database: AppDatabase): DonXinNghiDao {
        return database.donXinNghiDao()
    }

    @Provides
    @Singleton
    fun provideLichSuSuaDiemDao(database: AppDatabase): LichSuSuaDiemDao {
        return database.lichSuSuaDiemDao()
    }

    @Provides
    @Singleton
    fun provideViPhamDao(database: AppDatabase): ViPhamDao {
        return database.viPhamDao()
    }

    @Provides
    @Singleton
    fun provideHoaDonHocPhiDao(database: AppDatabase): HoaDonHocPhiDao {
        return database.hoaDonHocPhiDao()
    }

    @Provides
    @Singleton
    fun provideThongBaoDao(database: AppDatabase): ThongBaoDao {
        return database.thongBaoDao()
    }

    @Provides
    @Singleton
    fun provideTinNhanDao(database: AppDatabase): TinNhanDao {
        return database.tinNhanDao()
    }

    @Provides
    @Singleton
    fun provideThucDonDao(database: AppDatabase): ThucDonDao {
        return database.thucDonDao()
    }

    @Provides
    @Singleton
    fun provideSuatAnDao(database: AppDatabase): SuatAnDao {
        return database.suatAnDao()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.edumanage.example.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideVnEduApiService(retrofit: Retrofit): VnEduApiService {
        return retrofit.create(VnEduApiService::class.java)
    }
}
