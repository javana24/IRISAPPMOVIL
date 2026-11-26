package com.example.irisappmovil.data.local.chat


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId ORDER BY timestamp ASC")
    fun getMessages(conversationId: String): Flow<List<ChatMessageEntity>>

    @Query("SELECT * FROM chat_messages WHERE conversationId = :conversationId AND chatMode = :mode ORDER BY timestamp ASC")
    fun getMessagesByMode(conversationId: String, mode: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Delete
    suspend fun deleteMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId")
    suspend fun clearConversation(conversationId: String)

    @Query("DELETE FROM chat_messages WHERE conversationId = :conversationId AND chatMode = :mode")
    suspend fun clearConversationByMode(conversationId: String, mode: String)

    @Query("SELECT * FROM chat_messages ORDER BY timestamp DESC LIMIT 50")
    suspend fun getRecentMessages(): List<ChatMessageEntity>
}