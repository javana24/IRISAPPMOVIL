package com.example.irisappmovil.data.local.mood

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val mood: String, // CALM, SAFE, DOUBT, ALERT
    val timestamp: Long = System.currentTimeMillis()
)