package com.example.irisappmovil.ui.Splash


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irisappmovil.data.api.DojoRepository
import com.example.irisappmovil.data.api.UserRepository
import com.example.irisappmovil.di.notificacion.NotificationOrchestrator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SplashDestination { HOME, LOGIN }

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dojoRepository: DojoRepository,
    private val userRepository: UserRepository,
    private val notificationOrchestrator: NotificationOrchestrator
) : ViewModel() {
    private val _destination = MutableStateFlow(SplashDestination.LOGIN)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    fun initializeApp() {
        viewModelScope.launch {
            dojoRepository.ensureDefaultModules()
            notificationOrchestrator.scheduleDailyReminder()
            _destination.value = SplashDestination.HOME
        }
    }
}