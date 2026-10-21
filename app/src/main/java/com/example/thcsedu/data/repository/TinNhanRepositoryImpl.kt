package com.example.thcsedu.data.repository

import com.example.thcsedu.data.local.dao.TinNhanDao
import com.example.thcsedu.data.local.entity.TinNhanEntity
import com.example.thcsedu.domain.repository.TinNhanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TinNhanRepositoryImpl @Inject constructor(
    private val tinNhanDao: TinNhanDao
) : TinNhanRepository {

    override fun getMessagesBetween(user1: String, user2: String): Flow<List<TinNhanEntity>> {
        return tinNhanDao.getMessagesBetween(user1, user2)
    }

    override suspend fun sendMessage(message: TinNhanEntity) {
        tinNhanDao.insertMessage(message)
    }
}
