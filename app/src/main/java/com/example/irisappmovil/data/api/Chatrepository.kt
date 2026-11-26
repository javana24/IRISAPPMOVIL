package com.example.irisappmovil.data.api

import com.example.irisappmovil.data.firebase.FirestoreUserSyncRepository
import com.example.irisappmovil.data.local.chat.ChatMessageDao
import com.example.irisappmovil.data.local.chat.ChatMessageEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import retrofit2.Response

/** Resultado de enviar un mensaje al backend. */
data class ChatSendOutcome(
    val message: ChatMessageEntity,
    val safetyAlert: Boolean,
    val safetyMessage: String?,
    /** Banderas rojas del mensaje de la pareja simulada (vacío en el modo consejero). */
    val redFlags: List<RedFlagDto> = emptyList()
)

/** Mensajes de historial que se envían al modelo (el backend usa como mucho los últimos 20). */
private const val HISTORY_LIMIT = 20

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
        history: List<ChatMessageEntity>,
        difficulty: PartnerDifficulty = PartnerDifficulty.MODERADA
    ): Result<ChatSendOutcome> {
        val userMessage = ChatMessageEntity(
            conversationId = conversationId,
            role = "user",
            content = content,
            chatMode = mode
        )
        val savedUserMessage = userMessage.copy(id = chatMessageDao.insertMessage(userMessage))

        val result = try {
            val messages = history.takeLast(HISTORY_LIMIT).map { MessageDto(it.role, it.content) } +
                MessageDto("user", content)

            val response = apiService.sendMessage(
                ChatRequestDto(
                    messages = messages,
                    language = currentApiLanguage(),
                    simulatorMode = mode.toApiSimulatorMode(),
                    difficulty = difficulty.apiValue
                )
            )

            val body = response.body()
            if (response.isSuccessful && body != null) {
                val assistantMessage = ChatMessageEntity(
                    conversationId = conversationId,
                    role = "assistant",
                    content = body.response,
                    chatMode = mode
                )
                val savedAssistant = assistantMessage.copy(id = chatMessageDao.insertMessage(assistantMessage))
                firebaseAuth.currentUser?.uid?.let { uid ->
                    firestoreSync.pushMensajeIa(uid, savedUserMessage)
                    firestoreSync.pushMensajeIa(uid, savedAssistant)
                }

                val notice = body.toSafetyNotice()
                Result.success(
                    ChatSendOutcome(
                        message = savedAssistant,
                        safetyAlert = notice?.alertFlagged == true,
                        safetyMessage = notice?.customMessage,
                        redFlags = body.redFlags.orEmpty()
                    )
                )
            } else {
                Result.failure(IllegalStateException(errorMessageFrom(response)))
            }
        } catch (e: HttpException) {
            Result.failure(IllegalStateException("Error HTTP ${e.code()} en chat.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }

        // Si el envío falla, quitamos el mensaje para no dejar turnos huérfanos en el historial.
        if (result.isFailure) {
            chatMessageDao.deleteMessage(savedUserMessage)
        }
        return result
    }

    /** Pide al backend el informe de cómo ha respondido la persona en el simulador. */
    suspend fun analyzeConversation(
        history: List<ChatMessageEntity>,
        difficulty: PartnerDifficulty
    ): Result<AnalysisReportDto> = try {
        val response = apiService.analyzeConversation(
            AnalyzeRequestDto(
                messages = history.map { MessageDto(it.role, it.content) },
                language = currentApiLanguage(),
                difficulty = difficulty.apiValue
            )
        )
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Result.success(body)
        } else {
            Result.failure(IllegalStateException(errorMessageFrom(response)))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun clearConversation(conversationId: String) {
        chatMessageDao.clearConversation(conversationId)
    }

    suspend fun clearConversationByMode(conversationId: String, mode: String) {
        chatMessageDao.clearConversationByMode(conversationId, mode)
    }

    private fun errorMessageFrom(response: Response<*>): String {
        if (response.code() == 503) {
            return "La IA no está disponible en este momento. Vuelve a intentarlo en unos minutos."
        }
        val parsed = response.errorBody()?.string()
            ?.takeIf { it.isNotBlank() }
            ?.let { runCatching { gson.fromJson(it, ApiError::class.java) }.getOrNull() }
        return "HTTP ${response.code()}: ${parsed?.error ?: "El servicio de chat no respondió correctamente."}"
    }
}

internal fun currentApiLanguage(): String =
    if (Locale.getDefault().language.startsWith("en")) "en" else "es"

/**
 * Mapeo UI Android → `simulatorMode` del backend (`POST /api/chat`):
 * - pestaña Pareja: `VIRTUAL_PARTNER` → `"partner"`
 * - pestaña IRIS: cualquier otro valor → `"iris"`
 */
internal fun String.toApiSimulatorMode(): String = when (uppercase()) {
    "VIRTUAL_PARTNER" -> "partner"
    else -> "iris"
}
