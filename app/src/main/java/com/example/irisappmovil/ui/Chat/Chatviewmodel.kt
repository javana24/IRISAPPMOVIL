package com.example.irisappmovil.ui.Chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irisappmovil.data.api.AnalysisReportDto
import com.example.irisappmovil.data.api.ChatRepository
import com.example.irisappmovil.data.api.ChatSafetyNotice
import com.example.irisappmovil.data.api.PartnerDifficulty
import com.example.irisappmovil.data.api.RedFlagDto
import com.example.irisappmovil.data.api.UserRepository
import com.example.irisappmovil.data.api.parseSafetyNotice
import com.example.irisappmovil.data.local.chat.ChatMessageEntity
import com.iris.app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

const val MODE_IRIS = "IRIS"
const val MODE_PARTNER = "VIRTUAL_PARTNER"
private const val MIN_USER_MESSAGES_FOR_ANALYSIS = 2

data class ChatUiState(
    val mode: String = MODE_IRIS,
    val conversationId: String = "default",
    val messages: List<ChatMessageEntity> = emptyList(),
    val isSending: Boolean = false,
    val isLoadingMessages: Boolean = true,
    val errorMessage: String? = null,
    /** Aviso de seguridad del último envío (solo con riesgo real). */
    val safetyNotice: ChatSafetyNotice? = null,
    val isAuthenticated: Boolean = false,
    val difficulty: PartnerDifficulty = PartnerDifficulty.MODERADA,
    /** Banderas rojas por id de mensaje de la pareja (se guardan solo en memoria). */
    val redFlags: Map<Long, List<RedFlagDto>> = emptyMap(),
    val isAnalyzing: Boolean = false,
    val analysis: AnalysisReportDto? = null,
    val analysisError: String? = null
) {
    val isPartner: Boolean get() = mode == MODE_PARTNER

    val canAnalyze: Boolean
        get() = isPartner && !isSending && !isAnalyzing &&
            messages.count { it.role == "user" } >= MIN_USER_MESSAGES_FOR_ANALYSIS
}

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userRepository.isSignedIn().collect { signedIn ->
                _uiState.update { it.copy(isAuthenticated = signedIn) }
            }
        }
        viewModelScope.launch {
            _uiState
                .map { it.mode to it.conversationId }
                .distinctUntilChanged()
                .flatMapLatest { (mode, conversationId) ->
                    chatRepository.getMessagesByMode(conversationId, mode)
                }
                .collect { list ->
                    _uiState.update { it.copy(messages = list, isLoadingMessages = false) }
                }
        }
    }

    fun switchMode(mode: String) {
        _uiState.update {
            it.copy(mode = mode, errorMessage = null, safetyNotice = null, analysis = null, analysisError = null)
        }
    }

    fun dismissSafetyBanner() {
        _uiState.update { it.copy(safetyNotice = null) }
    }

    /** Cambiar la dificultad reinicia la conversación con la pareja. */
    fun setDifficulty(difficulty: PartnerDifficulty) {
        if (_uiState.value.difficulty == difficulty) return
        _uiState.update { it.copy(difficulty = difficulty) }
        clearConversation()
    }

    fun clearConversation() {
        viewModelScope.launch {
            val state = _uiState.value
            chatRepository.clearConversationByMode(state.conversationId, state.mode)
            _uiState.update {
                it.copy(
                    redFlags = if (state.isPartner) emptyMap() else it.redFlags,
                    analysis = null,
                    analysisError = null,
                    errorMessage = null,
                    safetyNotice = null
                )
            }
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank() || _uiState.value.isSending) return
        _uiState.update { it.copy(isSending = true, errorMessage = null) }

        viewModelScope.launch {
            userRepository.getCurrentUser().first()?.id?.let { uid ->
                userRepository.updateStreak(uid)
                if (_uiState.value.mode == MODE_IRIS) {
                    userRepository.addCalmMinutes(uid, 1)
                }
            }
            val state = _uiState.value
            val result = chatRepository.sendMessage(
                conversationId = state.conversationId,
                content = content,
                mode = state.mode,
                history = state.messages,
                difficulty = state.difficulty
            )
            result.fold(
                onSuccess = { outcome ->
                    val notice = parseSafetyNotice(outcome.safetyAlert, outcome.safetyMessage)
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            errorMessage = null,
                            safetyNotice = notice,
                            redFlags = if (outcome.redFlags.isNotEmpty()) {
                                it.redFlags + (outcome.message.id to outcome.redFlags)
                            } else {
                                it.redFlags
                            }
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isSending = false, errorMessage = mapSendError(e)) }
                }
            )
        }
    }

    fun analyzeConversation() {
        val state = _uiState.value
        if (!state.canAnalyze) return
        _uiState.update { it.copy(isAnalyzing = true, analysisError = null) }

        viewModelScope.launch {
            chatRepository.analyzeConversation(state.messages, state.difficulty).fold(
                onSuccess = { report -> _uiState.update { it.copy(isAnalyzing = false, analysis = report) } },
                onFailure = { e -> _uiState.update { it.copy(isAnalyzing = false, analysisError = mapSendError(e)) } }
            )
        }
    }

    fun dismissAnalysis() {
        _uiState.update { it.copy(analysis = null) }
    }

    private fun mapSendError(e: Throwable): String {
        val root = generateSequence(e) { it.cause }.last()
        if (root is UnknownHostException || root is ConnectException || root is SocketTimeoutException || root is IOException) {
            val apiUrl = appContext.getString(R.string.iris_api_base_url)
            return appContext.getString(R.string.chat_error_network, apiUrl)
        }
        return e.message?.takeIf { it.isNotBlank() }
            ?: appContext.getString(R.string.chat_error_generic)
    }
}
