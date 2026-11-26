package com.example.irisappmovil.ui.Auth

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irisappmovil.data.api.UserRepository
import com.example.irisappmovil.data.firebase.GoogleSignInHelper
import com.example.irisappmovil.data.local.preferences.UserPreferencesRepository
import com.example.irisappmovil.di.notificacion.IrisNotificationHelper
import com.example.irisappmovil.util.AuthUserErrorMapper
import com.example.irisappmovil.util.EmailValidation
import com.iris.app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLogged: Boolean = false,
    val emailInvalid: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val userRepository: UserRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val notificationHelper: IrisNotificationHelper,
    private val googleSignInHelper: GoogleSignInHelper
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    val languageTag: StateFlow<String> = preferencesRepository.preferencesFlow
        .map { it.languageTag }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "es")

    fun onEmailChanged(value: String) = _uiState.update {
        it.copy(
            email = value,
            errorMessage = null,
            emailInvalid = value.isNotBlank() && !EmailValidation.isValid(value.trim())
        )
    }

    fun onPasswordChanged(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }

    fun setLanguageTag(tag: String) = viewModelScope.launch {
        preferencesRepository.setLanguageTag(tag)
        withContext(Dispatchers.Main.immediate) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
        }
    }

    fun signInWithGoogle(activity: ComponentActivity) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val tokenResult = googleSignInHelper.getGoogleIdTokenForFirebase(activity)
        val authResult = tokenResult.fold(
            onSuccess = { token -> userRepository.signInWithGoogleIdToken(token) },
            onFailure = { Result.failure(it) }
        )
        if (authResult.isSuccess) {
            authResult.getOrNull()?.let { notificationHelper.showWelcomeNotification(it.name) }
        }
        val err = authResult.exceptionOrNull()
        _uiState.update {
            it.copy(
                isLoading = false,
                isLogged = authResult.isSuccess,
                errorMessage = if (AuthUserErrorMapper.shouldShowMessage(err)) {
                    AuthUserErrorMapper.messageFor(appContext, err)
                } else {
                    null
                }
            )
        }
    }

    fun login() = viewModelScope.launch {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        if (email.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(errorMessage = appContext.getString(R.string.auth_fill_credentials), isLoading = false)
            }
            return@launch
        }
        if (!EmailValidation.isValid(email)) {
            _uiState.update {
                it.copy(
                    errorMessage = appContext.getString(R.string.auth_invalid_email),
                    isLoading = false,
                    emailInvalid = true
                )
            }
            return@launch
        }
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        val result = userRepository.signInWithEmailPassword(email, password)
        if (result.isSuccess) {
            result.getOrNull()?.let { notificationHelper.showWelcomeNotification(it.name) }
        }
        val ex = result.exceptionOrNull()
        _uiState.update {
            it.copy(
                isLoading = false,
                isLogged = result.isSuccess,
                errorMessage = if (result.isFailure && AuthUserErrorMapper.shouldShowMessage(ex)) {
                    AuthUserErrorMapper.messageFor(appContext, ex)
                        ?: appContext.getString(R.string.auth_error_generic)
                } else {
                    null
                },
                emailInvalid = false
            )
        }
    }
}
