package com.example.irisappmovil.data.local.mood

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao {
    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getMoodHistory(userId: String): Flow<List<MoodEntryEntity>>

    @Query("SELECT * FROM mood_entries WHERE userId = :userId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastMood(userId: String): MoodEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodEntryEntity): Long

    @Delete
    suspend fun deleteMood(mood: MoodEntryEntity)

    @Query("DELETE FROM mood_entries WHERE userId = :userId")
    suspend fun clearMoodHistory(userId: String)
}