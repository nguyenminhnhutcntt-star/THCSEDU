package com.example.thcsedu.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BangDiemDto(
    val maHocSinh: String,
    val maMonHoc: String,
    val hocKy: Int,
    val namHoc: String,
    val dsDiemTX: List<Float>,
    val diemGiuaKy: Float?,
    val diemCuoiKy: Float?,
    val nhanXet: String?
)

@JsonClass(generateAdapter = true)
data class BangDiemSyncRequest(
    val changes: List<BangDiemDto>
)

@JsonClass(generateAdapter = true)
data class BangDiemSyncResponse(
    val success: Boolean,
    val message: String?,
    val syncedCount: Int
)
