package com.example.irisappmovil.data.api

/**
 * Aviso de seguridad del backend (`POST /api/chat`, SCRUM-65/66/71), separado del texto
 * conversacional en `response`.
 */
data class ChatSafetyNotice(
    val alertFlagged: Boolean,
    val customMessage: String?
)

/** Mapeo desde [ChatResponseDto] con la misma lógica que [ChatRepository.sendMessage]. */
fun ChatResponseDto.toSafetyNotice(): ChatSafetyNotice? =
    parseSafetyNotice(safetyAlert == true, safetyMessage?.takeIf { it.isNotBlank() })

fun parseSafetyNotice(safetyAlert: Boolean, safetyMessage: String?): ChatSafetyNotice? {
    val message = safetyMessage?.takeIf { it.isNotBlank() }
    if (!safetyAlert && message == null) return null
    return ChatSafetyNotice(alertFlagged = safetyAlert, customMessage = message)
}

fun ChatSafetyNotice?.shouldShowBanner(): Boolean = this != null
