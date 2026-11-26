package com.example.irisappmovil.data.api


import retrofit2.Response
import retrofit2.http.*

interface IrisApiService {

    // ─── Auth ─────────────────────────────────────────────────────────────────
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): Response<ApiResponse<LoginResponseDto>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // ─── User ─────────────────────────────────────────────────────────────────
    @GET("users/me")
    suspend fun getCurrentUser(): Response<ApiResponse<UserDto>>

    @PATCH("users/me")
    suspend fun updateUser(@Body user: Map<String, Any>): Response<ApiResponse<UserDto>>

    // ─── Chat ─────────────────────────────────────────────────────────────────
    @GET("api/health")
    suspend fun getHealth(): Response<Map<String, Boolean>>

    @POST("api/chat")
    suspend fun sendMessage(@Body request: ChatRequestDto): Response<ChatResponseDto>

    @GET("chat/history")
    suspend fun getChatHistory(
        @Query("mode") mode: String,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<ChatResponseDto>>>

    // ─── Dojo ─────────────────────────────────────────────────────────────────
    /** Ruta habitual en backends Node del proyecto. */
    @GET("dojo/modules")
    suspend fun getDojoModules(): Response<ApiResponse<List<DojoModuleDto>>>

    /** Alternativa si el servidor monta el router bajo `/api/`. */
    @GET("api/dojo/modules")
    suspend fun getDojoModulesUnderApi(): Response<ApiResponse<List<DojoModuleDto>>>

    @GET("dojo/modules/{moduleId}")
    suspend fun getModuleDetail(
        @Path("moduleId") moduleId: String
    ): Response<ApiResponse<DojoModuleDto>>

    @POST("dojo/modules/{moduleId}/complete")
    suspend fun completeModule(
        @Path("moduleId") moduleId: String,
        @Body score: Map<String, Int>
    ): Response<ApiResponse<Map<String, Any>>>

    // ─── Emergency ────────────────────────────────────────────────────────────
    @POST("emergency/alert")
    suspend fun sendEmergencyAlert(
        @Body alert: EmergencyAlertDto
    ): Response<ApiResponse<EmergencyResponseDto>>
}