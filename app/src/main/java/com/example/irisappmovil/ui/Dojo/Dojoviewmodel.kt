package com.example.irisappmovil.ui.Dojo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irisappmovil.data.api.DojoRepository
import com.example.irisappmovil.data.api.UserRepository
import com.example.irisappmovil.data.local.dojo.DojoModuleEntity
import com.example.irisappmovil.data.local.dojo.isCompleted
import com.example.irisappmovil.di.notificacion.IrisNotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

data class DojoUiState(
    val modules: List<DojoModuleEntity> = emptyList(),
    val totalProgress: Float = 0.64f,
    val nextMilestone: String = "Maestría en Límites",
    val lessonsToComplete: Int = 2,
    val isLoading: Boolean = true,
    val syncFromNetworkFailed: Boolean = false,
    val isAuthenticated: Boolean = false
)

@HiltViewModel
class DojoViewModel @Inject constructor(
    private val dojoRepository: DojoRepository,
    private val userRepository: UserRepository,
    private val notificationHelper: IrisNotificationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(DojoUiState())
    val uiState: StateFlow<DojoUiState> = _uiState.asStateFlow()

    private var syncFailNotified = false

    init {
        viewModelScope.launch {
            userRepository.isSignedIn().collect { signedIn ->
                _uiState.update { it.copy(isAuthenticated = signedIn) }
            }
        }
        loadModules()
    }

    private fun loadModules() {
        viewModelScope.launch {
            val networkOk = try {
                withTimeout(12_000L) {
                    dojoRepository.refreshModules()
                }
            } catch (e: TimeoutCancellationException) {
                dojoRepository.ensureDefaultModules()
                false
            }
            if (!networkOk && !syncFailNotified) {
                syncFailNotified = true
                notificationHelper.showDojoSyncReminderNotification()
            }
            dojoRepository.getAllModules().collect { modules ->
                val progress = dojoRepository.getProgress()
                val next = modules.firstOrNull { !it.isCompleted() }
                val remaining = modules.count { !it.isCompleted() }
                _uiState.update {
                    it.copy(
                        modules = modules,
                        totalProgress = progress,
                        nextMilestone = next?.title ?: "Dojo Dominado",
                        lessonsToComplete = remaining,
                        isLoading = false,
                        syncFromNetworkFailed = !networkOk
                    )
                }
            }
        }
    }
}
