package com.example.irisappmovil.data.api

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Verifica el contrato documentado en SCRUM-65/66/71 (`ChatResponseDto` + mapeo en [ChatRepository])
 * sin depender del backend en ejecución.
 */
class ChatSafetyContractTest {

    private val gson = Gson()

    @Test
    fun chatResponseDto_deserializesDocumentedSafetyFields() {
        val json = """
            {
              "response": "Texto del asistente",
              "safetyAlert": true,
              "safetyMessage": "Si estás en riesgo, contacta con el 016."
            }
        """.trimIndent()
        val dto = gson.fromJson(json, ChatResponseDto::class.java)
        assertEquals("Texto del asistente", dto.response)
        assertEquals(true, dto.safetyAlert)
        assertEquals("Si estás en riesgo, contacta con el 016.", dto.safetyMessage)
    }

    @Test
    fun chatResponseDto_omittedSafetyFieldsAreNull() {
        val dto = gson.fromJson("""{"response":"solo respuesta"}""", ChatResponseDto::class.java)
        assertNull(dto.safetyAlert)
        assertNull(dto.safetyMessage)
    }

    @Test
    fun repositoryOutcomeMapping_matchesChatRepositoryLogic() {
        val body = gson.fromJson(
            """{"response":"x","safetyAlert":true,"safetyMessage":""}""",
            ChatResponseDto::class.java
        )
        val notice = body.toSafetyNotice()
        assertNotNull(notice)
        assertTrue(notice!!.alertFlagged)
        assertNull(notice.customMessage)
    }

    @Test
    fun parseSafetyNotice_matchesDocumentedRule() {
        assertNull(parseSafetyNotice(safetyAlert = false, safetyMessage = null))
        assertNotNull(parseSafetyNotice(safetyAlert = true, safetyMessage = null))
        assertEquals("aviso", parseSafetyNotice(safetyAlert = false, safetyMessage = "aviso")?.customMessage)
        assertNull(parseSafetyNotice(safetyAlert = false, safetyMessage = ""))
    }

    @Test
    fun parseSafetyNotice_preservesBothFieldsWhenPresent() {
        val notice = parseSafetyNotice(safetyAlert = true, safetyMessage = "Contacta al 016.")
        assertNotNull(notice)
        assertTrue(notice!!.alertFlagged)
        assertEquals("Contacta al 016.", notice.customMessage)
    }

    @Test
    fun toApiSimulatorMode_mapsVirtualPartnerToPartner() {
        assertEquals("partner", "VIRTUAL_PARTNER".toApiSimulatorMode())
        assertEquals("iris", "IRIS".toApiSimulatorMode())
    }

    @Test
    fun toApiSimulatorMode_isCaseInsensitive() {
        assertEquals("partner", "virtual_partner".toApiSimulatorMode())
        assertEquals("iris", "iris".toApiSimulatorMode())
    }

    @Test
    fun chatResponseDto_deserializesRedFlagsAndRiskLevel() {
        val json = """
            {
              "response": "¿Con quién estabas? Mándame la ubicación.",
              "redFlags": [{"tactic": "vigilancia", "label": "Vigilancia del móvil o ubicación", "explanation": "Alex te pide la ubicación para vigilarte."}],
              "riskLevel": "none",
              "provider": "groq"
            }
        """.trimIndent()
        val dto = gson.fromJson(json, ChatResponseDto::class.java)
        assertEquals(1, dto.redFlags?.size)
        assertEquals("vigilancia", dto.redFlags!!.first().tactic)
        assertEquals("none", dto.riskLevel)
        assertNull(dto.safetyAlert)
    }

    @Test
    fun analysisReportDto_toleratesMissingLists() {
        val dto = gson.fromJson("""{"score": 72, "summary": "Buen trabajo"}""", AnalysisReportDto::class.java)
        assertEquals(72, dto.score)
        assertTrue(dto.strengths.orEmpty().isEmpty())
        assertTrue(dto.betterResponses.orEmpty().isEmpty())
    }

    @Test
    fun chatRequestDto_serializesDifficulty() {
        val json = gson.toJson(
            ChatRequestDto(
                messages = listOf(MessageDto("user", "Hola")),
                simulatorMode = "partner",
                difficulty = PartnerDifficulty.INTENSA.apiValue
            )
        )
        assertTrue(json.contains("\"difficulty\":\"intensa\""))
    }

}
