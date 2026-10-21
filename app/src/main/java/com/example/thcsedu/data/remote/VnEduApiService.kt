package com.example.thcsedu.data.remote

import com.example.thcsedu.data.remote.model.BangDiemDto
import com.example.thcsedu.data.remote.model.BangDiemSyncRequest
import com.example.thcsedu.data.remote.model.BangDiemSyncResponse
import com.example.thcsedu.data.remote.model.LoginRequest
import com.example.thcsedu.data.remote.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface VnEduApiService {

    @GET("api/bangdiem/{classId}")
    suspend fun getBangDiemByClass(
        @Path("classId") classId: String
    ): List<BangDiemDto>

    @POST("api/bangdiem/sync")
    suspend fun syncBangDiem(
        @Body request: BangDiemSyncRequest
    ): Response<BangDiemSyncResponse>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): LoginResponse
}
