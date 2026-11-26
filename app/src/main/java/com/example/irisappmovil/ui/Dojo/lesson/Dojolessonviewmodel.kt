package com.example.irisappmovil.ui.Dojo.lesson

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irisappmovil.data.api.DojoRepository
import com.example.irisappmovil.data.local.dojo.DojoQuestionBank
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
        val passed = score >= DojoQuestionBank.PASS_SCORE
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
        // Cada reintento usa preguntas nuevas al azar.
        _uiState.update {
            it.copy(
                questions = buildQuestionsForModule(moduleId),
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
        val moduleTitle = MODULE_TITLES[moduleId] ?: MODULE_TITLES.getValue("mod_01")
        val picked = DojoQuestionBank.pickForModule(moduleId)
        return picked.mapIndexed { index, question ->
            LessonQuestion(
                question = question.text,
                moduleNumber = moduleNumber,
                moduleTitle = moduleTitle,
                options = question.options.mapIndexed { optionIndex, option ->
                    val label = ('A' + optionIndex).toString()
                    LessonOption(label, label, option.text, option.feedback, option.correct)
                },
                progressPercent = ((index + 1) * 100) / picked.size
            )
        }
    }

    private companion object {
        val MODULE_TITLES = mapOf(
            "mod_01" to "RECONOCIENDO LA MANIPULACIÓN",
            "mod_02" to "LÍMITES SALUDABLES",
            "mod_03" to "COMUNICACIÓN ASERTIVA",
            "mod_04" to "ESPACIO SEGURO"
        )
    }
}
