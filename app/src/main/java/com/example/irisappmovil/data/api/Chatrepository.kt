package com.example.irisappmovil.data.api


import com.example.irisappmovil.data.firebase.FirestoreUserSyncRepository
import com.example.irisappmovil.data.local.chat.ChatMessageDao
import com.example.irisappmovil.data.local.chat.ChatMessageEntity
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton

/** Resultado de enviar mensaje al backend, incluyendo aviso de seguridad del API (SCRUM-71). */
data class ChatSendOutcome(
    val message: ChatMessageEntity,
    val safetyAlert: Boolean,
    val safetyMessage: String?
)

@Singleton
class ChatRepository @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val apiService: IrisApiService,
    private val firebaseAuth: FirebaseAuth,
    private val firestoreSync: FirestoreUserSyncRepository
) {
    private val gson = Gson()

    fun getMessages(conversationId: String): Flow<List<ChatMessageEntity>> =
        chatMessageDao.getMessages(conversationId)

    fun getMessagesByMode(conversationId: String, mode: String): Flow<List<ChatMessageEntity>> =
        chatMessageDao.getMessagesByMode(conversationId, mode)

    suspend fun sendMessage(
        conversationId: String,
        content: String,
        mode: String,
        history: List<ChatMessageEntity>
    ): Result<ChatSendOutcome> {
        // Save user message
        val userMessage = ChatMessageEntity(
            conversationId = conversationId,
            role = "user",
            content = content,
            chatMode = mode
        )
        val userRowId = chatMessageDao.insertMessage(userMessage)
        val uid = firebaseAuth.currentUser?.uid
        if (uid != null) {
            firestoreSync.pushMensajeIa(uid, userMessage.copy(id = userRowId))
        }

        return try {
            val messages = history.takeLast(10).map { MessageDto(it.role, it.content) } +
                listOf(MessageDto("user", content))

            val response = apiService.sendMessage(
                ChatRequestDto(
                    messages = messages,
                    simulatorMode = mode.toApiSimulatorMode()
                )
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                val assistantContent = body.response
                val assistantMessage = ChatMessageEntity(
                    conversationId = conversationId,
                    role = "assistant",
                    content = assistantContent,
                    chatMode = mode
                )
                val asstRowId = chatMessageDao.insertMessage(assistantMessage)
                if (uid != null) {
                    firestoreSync.pushMensajeIa(uid, assistantMessage.copy(id = asstRowId))
                }

                val notice = body.toSafetyNotice()
                Result.success(
                    ChatSendOutcome(
                        message = assistantMessage,
                        safetyAlert = notice?.alertFlagged == true,
                        safetyMessage = notice?.customMessage
                    )
                )
            } else {
                val rawError = response.errorBody()?.string()
                val parsedError = rawError
                    ?.takeIf { it.isNotBlank() }
                    ?.let { runCatching { gson.fromJson(it, ApiError::class.java) }.getOrNull() }
                val message = parsedError?.error ?: "El servicio de chat no respondió correctamente."
                Result.failure(IllegalStateException("HTTP ${response.code()}: $message"))
            }
        } catch (e: HttpException) {
            Result.failure(IllegalStateException("Error HTTP ${e.code()} en chat.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearConversation(conversationId: String) {
        chatMessageDao.clearConversation(conversationId)
    }

    suspend fun clearConversationByMode(conversationId: String, mode: String) {
        chatMessageDao.clearConversationByMode(conversationId, mode)
    }
}

/**
 * Mapeo UI Android → `simulatorMode` del backend (`POST /api/chat`, SCRUM-65/66/71):
 * - [ChatUiState] / pestaña Partner: `VIRTUAL_PARTNER` → `"partner"`
 * - pestaña IRIS: cualquier otro valor (p. ej. `IRIS`) → `"iris"`
 */
internal fun String.toApiSimulatorMode(): String = when (uppercase()) {
    "VIRTUAL_PARTNER" -> "partner"
    else -> "iris"
}