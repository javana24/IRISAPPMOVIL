package com.example.irisappmovil.ui.Profile

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irisappmovil.data.api.UserRepository
import com.example.irisappmovil.data.firebase.FirestoreUserSyncRepository
import com.example.irisappmovil.data.local.preferences.UserPreferences
import com.example.irisappmovil.data.local.preferences.UserPreferencesRepository
import com.example.irisappmovil.data.local.usuarios.UserEntity
import com.example.irisappmovil.util.EmailValidation
import com.example.irisappmovil.di.notificacion.IrisNotificationHelper
import com.example.irisappmovil.di.notificacion.NotificationOrchestrator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.iris.app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.ceil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class ProfileUiState(
    val user: UserEntity? = null,
    val preferences: UserPreferences = UserPreferences(),
    val isLoading: Boolean = true,
    val profileNameInput: String = "",
    val profileEmailInput: String = "",
    val profileSavedMessage: String? = null
)

sealed class ProfileDialogEvent {
    data class CooldownNombre(val diasRestantes: Int) : ProfileDialogEvent()
    data class CooldownCorreo(val diasRestantes: Int) : ProfileDialogEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val preferencesRepository: UserPreferencesRepository,
    private val notificationOrchestrator: NotificationOrchestrator,
    private val notificationHelper: IrisNotificationHelper,
    private val firestoreSync: FirestoreUserSyncRepository,
    private val firebaseAuth: FirebaseAuth,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    private val _formState = MutableStateFlow(ProfileUiState())
    private val _profileDialog = MutableStateFlow<ProfileDialogEvent?>(null)
    val profileDialog: StateFlow<ProfileDialogEvent?> = _profileDialog.asStateFlow()

    val uiState: StateFlow<ProfileUiState> = combine(
        userRepository.getCurrentUser(),
        preferencesRepository.preferencesFlow,
        _formState
    ) { user, prefs, form ->
        ProfileUiState(
            user = user,
            preferences = prefs,
            isLoading = false,
            profileNameInput = if (form.profileNameInput.isBlank()) (user?.name ?: "") else form.profileNameInput,
            profileEmailInput = if (form.profileEmailInput.isBlank()) (user?.email ?: "") else form.profileEmailInput,
            profileSavedMessage = form.profileSavedMessage
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProfileUiState()
    )

    fun dismissProfileDialog() {
        _profileDialog.value = null
    }

    fun setDarkMode(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.setDarkMode(enabled)
    }

    fun setNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.setNotificationsEnabled(enabled)
        if (!enabled) {
            preferencesRepository.setDailyReminderEnabled(false)
            preferencesRepository.setProgressNotificationsEnabled(false)
            notificationOrchestrator.cancelDailyReminder()
        } else {
            preferencesRepository.setDailyReminderEnabled(true)
            preferencesRepository.setProgressNotificationsEnabled(true)
            notificationOrchestrator.scheduleDailyReminder()
        }
    }

    fun setDailyReminderEnabled(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.setDailyReminderEnabled(enabled)
        if (enabled) notificationOrchestrator.scheduleDailyReminder() else notificationOrchestrator.cancelDailyReminder()
    }

    fun setProgressNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        preferencesRepository.setProgressNotificationsEnabled(enabled)
    }

    fun setLanguage(tag: String) = viewModelScope.launch {
        preferencesRepository.setLanguageTag(tag)
        withContext(Dispatchers.Main.immediate) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
        }
    }

    fun logout(onFinished: () -> Unit = {}) = viewModelScope.launch {
        userRepository.logout()
        preferencesRepository.setAvatarUri(null)
        onFinished()
    }

    /** Elimina la cuenta y todos sus datos. `onError` recibe un mensaje para la persona usuaria. */
    fun deleteAccount(onDeleted: () -> Unit, onError: (String) -> Unit) = viewModelScope.launch {
        userRepository.deleteAccount().fold(
            onSuccess = {
                preferencesRepository.setAvatarUri(null)
                onDeleted()
            },
            onFailure = { e -> onError(e.message ?: "No se pudo eliminar la cuenta. Inténtalo de nuevo.") }
        )
    }

    fun updateAvatar(uriString: String?) = viewModelScope.launch {
        if (uriString == null) {
            preferencesRepository.setAvatarUri(null)
            userRepository.updateAvatar(null)
            return@launch
        }
        val localUri = try {
            withContext(Dispatchers.IO) {
                val uri = android.net.Uri.parse(uriString)
                val inputStream = appContext.contentResolver.openInputStream(uri)
                if (inputStream != null) {
                    val avatarFile = java.io.File(appContext.filesDir, "avatar_profile.jpg")
                    avatarFile.outputStream().use { outputStream ->
                        inputStream.use { it.copyTo(outputStream) }
                    }
                    android.net.Uri.fromFile(avatarFile).toString()
                } else {
                    uriString
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            uriString
        }
        preferencesRepository.setAvatarUri(localUri)
        userRepository.updateAvatar(localUri)
    }

    fun onNameChanged(value: String) {
        _formState.update { it.copy(profileNameInput = value, profileSavedMessage = null) }
    }

    fun onEmailChanged(value: String) {
        _formState.update { it.copy(profileEmailInput = value, profileSavedMessage = null) }
    }

    fun saveProfile() = viewModelScope.launch {
        val firebaseUser = firebaseAuth.currentUser
        val localUser = userRepository.getCurrentUser().first()

        val name = _formState.value.profileNameInput.trim().ifBlank { localUser?.name ?: "" }
        val emailDesired = _formState.value.profileEmailInput.trim().ifBlank { localUser?.email ?: "" }

        if (name.isBlank() || emailDesired.isBlank()) {
            _formState.update { it.copy(profileSavedMessage = appContext.getString(R.string.profile_save_error)) }
            return@launch
        }
        if (!EmailValidation.isValid(emailDesired)) {
            _formState.update { it.copy(profileSavedMessage = appContext.getString(R.string.auth_invalid_email)) }
            return@launch
        }

        if (firebaseUser == null) {
            if (localUser == null) {
                _formState.update { it.copy(profileSavedMessage = appContext.getString(R.string.profile_save_error)) }
                return@launch
            }
            userRepository.createOrUpdateLocalUser(name = name, email = emailDesired)
            _formState.update {
                it.copy(
                    profileSavedMessage = appContext.getString(R.string.profile_save_success),
                    profileNameInput = "",
                    profileEmailInput = ""
                )
            }
            notificationHelper.showProfileSavedNotification()
            return@launch
        }

        val uid = firebaseUser.uid
        val campos = firestoreSync.fetchUsuarioCamposPerfil(uid)
        val now = System.currentTimeMillis()
        val cooldownMs = 14L * 24 * 60 * 60 * 1000
        val authEmail = firebaseUser.email.orEmpty()
        val nameChanged = name != (localUser?.name ?: "")
        val emailChanged = emailDesired.lowercase() != authEmail.lowercase()

        if (nameChanged) {
            val last = campos?.ultimoCambioNombreMs
            if (last != null && now - last < cooldownMs) {
                _profileDialog.value = ProfileDialogEvent.CooldownNombre(diasRestantes(last, now))
                return@launch
            }
        }
        if (emailChanged) {
            val last = campos?.ultimoCambioCorreoMs
            if (last != null && now - last < cooldownMs) {
                _profileDialog.value = ProfileDialogEvent.CooldownCorreo(diasRestantes(last, now))
                return@launch
            }
        }

        @Suppress("DEPRECATION")
        if (emailChanged) {
            runCatching {
                firebaseUser.updateEmail(emailDesired).await()
            }.onFailure { e ->
                _formState.update {
                    it.copy(profileSavedMessage = e.message ?: appContext.getString(R.string.profile_save_error))
                }
                return@launch
            }
            firestoreSync.marcarCambioCorreo(uid, now)
        }

        if (nameChanged) {
            runCatching {
                firebaseUser.updateProfile(
                    UserProfileChangeRequest.Builder().setDisplayName(name).build()
                ).await()
            }.onFailure { e ->
                _formState.update {
                    it.copy(profileSavedMessage = e.message ?: appContext.getString(R.string.profile_save_error))
                }
                return@launch
            }
            firestoreSync.marcarCambioNombre(uid, now)
        }

        runCatching { firebaseUser.reload().await() }
        val mailActualizado = firebaseAuth.currentUser?.email?.trim()?.takeIf { it.isNotBlank() }
            ?: emailDesired
        val base = localUser ?: return@launch
        val merged = base.copy(
            name = name,
            email = mailActualizado.trim()
        )
        userRepository.replaceCurrentUser(merged)

        _formState.update {
            it.copy(
                profileSavedMessage = appContext.getString(R.string.profile_save_success),
                profileNameInput = "",
                profileEmailInput = ""
            )
        }
        notificationHelper.showProfileSavedNotification()
    }

    private fun diasRestantes(ultimoMs: Long, ahora: Long): Int {
        val cooldownMs = 14L * 24 * 60 * 60 * 1000
        val restante = cooldownMs - (ahora - ultimoMs)
        if (restante <= 0) return 0
        return ceil(restante / (24.0 * 60 * 60 * 1000)).toInt().coerceAtLeast(1)
    }
}
