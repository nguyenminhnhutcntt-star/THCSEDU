package com.example.thcsedu.domain.repository

import com.example.thcsedu.data.local.entity.TinNhanEntity
import kotlinx.coroutines.flow.Flow

interface TinNhanRepository {
    fun getMessagesBetween(user1: String, user2: String): Flow<List<TinNhanEntity>>
    suspend fun sendMessage(message: TinNhanEntity)
}
