package com.example.thcsedu.data.remote.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val username: String,
    val password: String,
    val role: String
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val token: String,
    val role: String,
    val message: String?
)
