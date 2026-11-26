package com.example.irisappmovil.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/** Endpoints del backend iris-node que usa la app. No añadas rutas que el backend no tenga. */
interface IrisApiService {

    /** `{ ok: Boolean, providers: [...] }` — 503 si ningún proveedor de IA está disponible. */
    @GET("api/health")
    suspend fun getHealth(): Response<Map<String, Any>>

    @POST("api/chat")
    suspend fun sendMessage(@Body request: ChatRequestDto): Response<ChatResponseDto>

    /** Informe final del simulador de pareja celosa. */
    @POST("api/analyze")
    suspend fun analyzeConversation(@Body request: AnalyzeRequestDto): Response<AnalysisReportDto>
}
