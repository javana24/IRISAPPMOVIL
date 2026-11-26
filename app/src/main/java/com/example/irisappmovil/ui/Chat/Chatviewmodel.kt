package com.example.irisappmovil.ui.Chat

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iris.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import com.example.irisappmovil.data.api.ChatRepository
import com.example.irisappmovil.data.api.ChatSafetyNotice
import com.example.irisappmovil.data.api.parseSafetyNotice
import com.example.irisappmovil.data.api.UserRepository
import com.example.irisappmovil.data.local.chat.ChatMessageEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi

data class ChatUiState(
    val mode: String = "IRIS",
    val conversationId: String = "default",
    val messages: List<ChatMessageEntity> = emptyList(),
    val isSending: Boolean = false,
    val isLoadingMessages: Boolean = true,
    val errorMessage: String? = null,
    /** Aviso de seguridad del último envío; independiente del hilo de mensajes (SCRUM-71). */
    val safetyNotice: ChatSafetyNotice? = null,
    val isAuthenticated: Boolean = false
)

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
        _uiState.update { it.copy(mode = mode, errorMessage = null, safetyNotice = null) }
    }

    fun dismissSafetyBanner() {
        _uiState.update { it.copy(safetyNotice = null) }
    }

    fun clearConversation() {
        viewModelScope.launch {
            chatRepository.clearConversationByMode(_uiState.value.conversationId, _uiState.value.mode)
        }
    }

    fun sendMessage(content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            userRepository.getCurrentUser().first()?.id?.let { uid ->
                userRepository.updateStreak(uid)
                if (_uiState.value.mode == "IRIS") {
                    userRepository.addCalmMinutes(uid, 1)
                }
            }
            _uiState.update { it.copy(isSending = true, errorMessage = null) }
            val result = chatRepository.sendMessage(
                conversationId = _uiState.value.conversationId,
                content = content,
                mode = _uiState.value.mode,
                history = _uiState.value.messages
            )
            result.fold(
                onSuccess = { outcome ->
                    val notice = parseSafetyNotice(outcome.safetyAlert, outcome.safetyMessage)
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            errorMessage = null,
                            safetyNotice = notice
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isSending = false,
                            errorMessage = mapSendError(e)
                        )
                    }
                }
            )
        }
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
