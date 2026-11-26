package com.example.irisappmovil.data.api


import com.google.gson.annotations.SerializedName

// ─── Chat DTOs (contrato canónico SCRUM-65/SCRUM-66) ─────────────────────────
data class ChatRequestDto(
    @SerializedName("messages") val messages: List<MessageDto>,
    @SerializedName("language") val language: String = "es",
    @SerializedName("simulatorMode") val simulatorMode: String = "iris"
)

data class MessageDto(
    @SerializedName("role") val role: String,
    @SerializedName("text") val text: String
)

data class ChatResponseDto(
    @SerializedName("response") val response: String,
    @SerializedName("safetyAlert") val safetyAlert: Boolean? = null,
    @SerializedName("safetyMessage") val safetyMessage: String? = null
)

// ─── User DTOs ────────────────────────────────────────────────────────────────
data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("rank") val rank: String,
    @SerializedName("total_xp") val totalXp: Int,
    @SerializedName("current_streak") val currentStreak: Int,
    @SerializedName("level") val level: Int
)

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponseDto(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserDto
)

// ─── Dojo DTOs ────────────────────────────────────────────────────────────────
data class DojoModuleDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("icon_type") val iconType: String,
    @SerializedName("status") val status: String,
    @SerializedName("order") val order: Int,
    @SerializedName("xp_reward") val xpReward: Int,
    @SerializedName("lessons") val lessons: List<LessonDto> = emptyList()
)

data class LessonDto(
    @SerializedName("id") val id: String,
    @SerializedName("question") val question: String,
    @SerializedName("module_number") val moduleNumber: Int,
    @SerializedName("module_title") val moduleTitle: String,
    @SerializedName("options") val options: List<LessonOptionDto>
)

data class LessonOptionDto(
    @SerializedName("id") val id: String,
    @SerializedName("label") val label: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("is_correct") val isCorrect: Boolean
)

// ─── Emergency DTOs ───────────────────────────────────────────────────────────
data class EmergencyAlertDto(
    @SerializedName("user_id") val userId: String,
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("timestamp") val timestamp: Long = System.currentTimeMillis()
)

data class EmergencyResponseDto(
    @SerializedName("alert_id") val alertId: String,
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)

data class ApiErrorDetail(
    @SerializedName("field") val field: String?,
    @SerializedName("message") val message: String
)

data class ApiError(
    @SerializedName("error") val error: String,
    @SerializedName("details") val details: List<ApiErrorDetail>? = null
)

// ─── API Response wrapper ─────────────────────────────────────────────────────
data class ApiResponse<T>(
    @SerializedName("data") val data: T?,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?
)