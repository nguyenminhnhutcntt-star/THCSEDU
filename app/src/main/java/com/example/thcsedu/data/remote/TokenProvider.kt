package com.example.thcsedu.data.remote

interface TokenProvider {
    suspend fun getToken(): String?
}
