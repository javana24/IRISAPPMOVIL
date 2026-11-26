package com.example.irisappmovil.ui.Home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.irisappmovil.data.firebase.FirestoreUserSyncRepository
import com.example.irisappmovil.data.local.mood.MoodDao
import com.example.irisappmovil.data.local.mood.MoodEntryEntity
import com.example.irisappmovil.data.local.usuarios.UserEntity
import com.example.irisappmovil.data.api.UserRepository
import com.example.irisappmovil.data.local.preferences.UserPreferencesRepository
import com.example.irisappmovil.di.notificacion.IrisNotificationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: UserEntity? = null,
    val isSignedIn: Boolean = false,
    val avatarUri: String? = null,
    val selectedMood: String? = null,
    val dailyProgressPercent: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val moodDao: MoodDao,
    private val preferencesRepository: UserPreferencesRepository,
    private val notificationHelper: IrisNotificationHelper,
    private val firebaseAuth: FirebaseAuth,
    private val firestoreSync: FirestoreUserSyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadUser()
    }

    private fun loadUser() {
        viewModelScope.launch {
            combine(
                userRepository.getCurrentUser(),
                userRepository.isSignedIn(),
                preferencesRepository.preferencesFlow
            ) { user, signedIn, prefs ->
                Triple(user, signedIn, prefs.avatarUri)
            }.collect { (user, signedIn, avatarUri) ->
                user?.id?.let { uid ->
                    userRepository.updateStreak(uid)
                }
                _uiState.update {
                    it.copy(
                        user = user,
                        isSignedIn = signedIn,
                        avatarUri = avatarUri,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun selectMood(mood: String) {
        _uiState.update { it.copy(selectedMood = mood) }
        viewModelScope.launch {
            val userId = _uiState.value.user?.id ?: return@launch
            moodDao.insertMood(
                MoodEntryEntity(
                    userId = userId,
                    mood = mood
                )
            )
            firebaseAuth.currentUser?.uid?.let { uid ->
                firestoreSync.pushEntradaEstadoAnimo(
                    uid,
                    MoodEntryEntity(userId = userId, mood = mood)
                )
            }
            notificationHelper.showMoodSavedNotification()
        }
    }
}