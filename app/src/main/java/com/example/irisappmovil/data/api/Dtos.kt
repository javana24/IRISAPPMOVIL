package com.example.irisappmovil.data.api

import com.google.gson.annotations.SerializedName

// Contrato del backend iris-node (https://github.com/javana24/iris-node).
// Gson no usa los valores por defecto de Kotlin al deserializar: los campos opcionales son nullables.

// ─── POST /api/chat ───────────────────────────────────────────────────────────
data class ChatRequestDto(
    @SerializedName("messages") val messages: List<MessageDto>,
    @SerializedName("language") val language: String = "es",
    @SerializedName("simulatorMode") val simulatorMode: String = "iris",
    /** Solo para el simulador de pareja: "sutil" | "moderada" | "intensa". */
    @SerializedName("difficulty") val difficulty: String = PartnerDifficulty.MODERADA.apiValue
)

data class MessageDto(
    @SerializedName("role") val role: String,
    @SerializedName("text") val text: String
)

/** Táctica de control que ha usado la pareja simulada en su mensaje ("bandera roja"). */
data class RedFlagDto(
    @SerializedName("tactic") val tactic: String,
    @SerializedName("label") val label: String,
    @SerializedName("explanation") val explanation: String
)

data class ChatResponseDto(
    @SerializedName("response") val response: String,
    @SerializedName("redFlags") val redFlags: List<RedFlagDto>? = null,
    /** "none" | "concern" | "urgent" */
    @SerializedName("riskLevel") val riskLevel: String? = null,
    @SerializedName("safetyAlert") val safetyAlert: Boolean? = null,
    @SerializedName("safetyMessage") val safetyMessage: String? = null
)

// ─── POST /api/analyze ────────────────────────────────────────────────────────
data class AnalyzeRequestDto(
    @SerializedName("messages") val messages: List<MessageDto>,
    @SerializedName("language") val language: String = "es",
    @SerializedName("difficulty") val difficulty: String = PartnerDifficulty.MODERADA.apiValue
)

data class TacticDetectedDto(
    @SerializedName("tactic") val tactic: String,
    @SerializedName("label") val label: String,
    @SerializedName("example") val example: String?
)

data class BetterResponseDto(
    @SerializedName("partnerMessage") val partnerMessage: String,
    @SerializedName("suggestion") val suggestion: String
)

data class AnalysisReportDto(
    @SerializedName("score") val score: Int,
    @SerializedName("summary") val summary: String,
    @SerializedName("strengths") val strengths: List<String>? = null,
    @SerializedName("improvements") val improvements: List<String>? = null,
    @SerializedName("tacticsDetected") val tacticsDetected: List<TacticDetectedDto>? = null,
    @SerializedName("betterResponses") val betterResponses: List<BetterResponseDto>? = null
)

// ─── Errores ──────────────────────────────────────────────────────────────────
data class ApiErrorDetail(
    @SerializedName("field") val field: String?,
    @SerializedName("message") val message: String
)

data class ApiError(
    @SerializedName("error") val error: String,
    @SerializedName("details") val details: List<ApiErrorDetail>? = null
)

/** Niveles del simulador de pareja celosa. */
enum class PartnerDifficulty(val apiValue: String, val label: String, val description: String) {
    SUTIL("sutil", "Sutil", "Control disfrazado de cariño y preocupación. Lo más difícil de detectar."),
    MODERADA("moderada", "Moderada", "Celos claros, te pide explicaciones y te hace sentir culpable."),
    INTENSA("intensa", "Intensa", "Control fuerte: ultimátums, chantaje emocional y aislamiento.")
}
