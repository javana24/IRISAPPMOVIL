package com.example.irisappmovil.ui.Dojo.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irisappmovil.data.api.DojoRepository
import com.example.irisappmovil.di.notificacion.NotificationOrchestrator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LessonOption(
    val id: String,
    val label: String,
    val title: String,
    val description: String,
    val isCorrect: Boolean
)

data class LessonQuestion(
    val question: String,
    val moduleNumber: Int,
    val moduleTitle: String,
    val options: List<LessonOption>,
    val progressPercent: Int
)

data class LessonUiState(
    val questions: List<LessonQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionId: String? = null,
    val isAnswerRevealed: Boolean = false,
    val isLoading: Boolean = true,
    val correctAnswers: Set<Int> = emptySet(),
    val hasFailedCompletion: Boolean = false
) {
    val currentQuestion: LessonQuestion?
        get() = questions.getOrNull(currentIndex)
    val totalQuestions: Int
        get() = questions.size
    val progressPercent: Int
        get() = if (questions.isEmpty()) 0 else (((currentIndex + 1).toFloat() / questions.size) * 100).toInt()
}

@HiltViewModel
class DojoLessonViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dojoRepository: DojoRepository,
    private val notificationOrchestrator: NotificationOrchestrator
) : ViewModel() {

    private val moduleId: String = savedStateHandle.get<String>("moduleId") ?: ""

    private val _uiState = MutableStateFlow(LessonUiState())
    val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

    init {
        loadLesson()
    }

    private fun loadLesson() {
        val moduleQuestions = buildQuestionsForModule(moduleId)
        _uiState.update { it.copy(questions = moduleQuestions, isLoading = false) }
    }

    fun selectOption(optionId: String) {
        if (_uiState.value.isAnswerRevealed) return
        _uiState.update { it.copy(selectedOptionId = optionId) }
    }

    fun revealAnswer() {
        val selected = _uiState.value.selectedOptionId ?: return
        val currentIndex = _uiState.value.currentIndex
        val isCorrect = _uiState.value.currentQuestion?.options
            ?.find { it.id == selected }?.isCorrect == true
        _uiState.update {
            it.copy(
                isAnswerRevealed = true,
                correctAnswers = if (isCorrect) it.correctAnswers + currentIndex else it.correctAnswers
            )
        }
    }

    fun goNextQuestionOrFinish(onFinished: (score: Int, passed: Boolean, xpGained: Int) -> Unit) {
        val state = _uiState.value
        if (!state.isAnswerRevealed) return

        if (state.currentIndex < state.totalQuestions - 1) {
            _uiState.update {
                it.copy(
                    currentIndex = it.currentIndex + 1,
                    selectedOptionId = null,
                    isAnswerRevealed = false
                )
            }
            return
        }

        val score = calculateScore()
        val passed = score >= 70
        if (!passed) {
            _uiState.update { it.copy(hasFailedCompletion = true) }
            onFinished(score, false, 0)
            return
        }

        viewModelScope.launch {
            val xpGained = dojoRepository.completeModule(moduleId, score).getOrElse { 0 }
            notificationOrchestrator.triggerLevelCompleted(xpGained)
            onFinished(score, true, xpGained)
        }
    }

    fun retryLesson() {
        _uiState.update {
            it.copy(
                currentIndex = 0,
                selectedOptionId = null,
                isAnswerRevealed = false,
                correctAnswers = emptySet(),
                hasFailedCompletion = false
            )
        }
    }

    fun calculateScore(): Int {
        val correct = _uiState.value.correctAnswers.size
        val total = _uiState.value.totalQuestions.coerceAtLeast(1)
        return ((correct.toFloat() / total.toFloat()) * 100).toInt()
    }

    private fun buildQuestionsForModule(moduleId: String): List<LessonQuestion> {
        val moduleNumber = moduleId.filter { it.isDigit() }.toIntOrNull() ?: 1
        return listOf(
            LessonQuestion(
                question = "Tu pareja revisa constantemente tus mensajes y cuestiona tu memoria sobre eventos pasados. ¿Qué comportamiento es este?",
                moduleNumber = moduleNumber,
                moduleTitle = "MANIPULACIÓN PSICOLÓGICA",
                options = listOf(
                    LessonOption("A", "A", "Protección excesiva", "Interés genuino por tu seguridad digital.", false),
                    LessonOption("B", "B", "Gaslighting y control", "Manipulación para invalidar tu percepción y autonomía.", true),
                    LessonOption("C", "C", "Inseguridad temporal", "Falta de confianza puntual basada en el pasado.", false),
                    LessonOption("D", "D", "Transparencia radical", "Acuerdo mutuo de honestidad sin límites.", false),
                ),
                progressPercent = 33
            ),
            LessonQuestion(
                question = "Si alguien insiste en decidir por ti con la excusa de cuidarte, ¿qué señal prioritaria debes identificar?",
                moduleNumber = moduleNumber,
                moduleTitle = "LÍMITES SALUDABLES",
                options = listOf(
                    LessonOption("A", "A", "Romanticismo intenso", "Muestra de amor a largo plazo.", false),
                    LessonOption("B", "B", "Control encubierto", "Sustitución de tu criterio por imposición progresiva.", true),
                    LessonOption("C", "C", "Comunicación directa", "Diálogo sano para tomar decisiones.", false),
                    LessonOption("D", "D", "Apoyo logístico", "Ayuda puntual sin presión.", false),
                ),
                progressPercent = 66
            ),
            LessonQuestion(
                question = "Ante una discusión, ¿cuál respuesta es más asertiva para proteger tus límites?",
                moduleNumber = moduleNumber,
                moduleTitle = "COMUNICACIÓN ASERTIVA",
                options = listOf(
                    LessonOption("A", "A", "Ceder para evitar conflicto", "Prioriza la calma inmediata sobre tu seguridad.", false),
                    LessonOption("B", "B", "Guardar silencio total", "No expresa necesidad ni límite.", false),
                    LessonOption("C", "C", "Nombrar el límite y la consecuencia", "Comunicación clara, firme y sin agresión.", true),
                    LessonOption("D", "D", "Responder con ataque", "Escala la situación y te expone más.", false),
                ),
                progressPercent = 100
            )
        )
    }
}