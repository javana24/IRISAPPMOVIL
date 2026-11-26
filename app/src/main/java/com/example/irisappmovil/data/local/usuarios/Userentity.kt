package com.example.irisappmovil.data.local.usuarios

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val rank: String = "Novice",
    val totalXp: Int = 0,
    val currentStreak: Int = 0,
    val totalMinutesCalm: Int = 0,
    val level: Int = 1,
    val isActive: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)