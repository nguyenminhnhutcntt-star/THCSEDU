package com.example.thcsedu.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tin_nhan")
data class TinNhanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val senderId: String, // e.g. "PARENT" or "TEACHER"
    val receiverId: String,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis()
)
