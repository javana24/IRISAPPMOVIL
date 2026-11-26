package com.example.irisappmovil.data.api

import com.example.irisappmovil.data.firebase.FirestoreUserSyncRepository
import com.example.irisappmovil.data.firebase.GoogleSignInHelper
import com.example.irisappmovil.data.local.usuarios.UserDao
import com.example.irisappmovil.data.local.usuarios.UserEntity
import com.example.irisappmovil.domain.LevelSystem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import java.util.UUID
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await

@Singleton
class UserRepository @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val userDao: UserDao,
    private val firebaseAuth: FirebaseAuth,
    private val firestoreSync: FirestoreUserSyncRepository,
    private val googleSignInHelper: GoogleSignInHelper
) {
    fun getCurrentUser(): Flow<UserEntity?> = userDao.getCurrentUser()

    /** Sesión de producto: solo Firebase (SCRUM-76). */
    fun isSignedIn(): Flow<Boolean> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }
        firebaseAuth.addAuthStateListener(listener)
        trySend(firebaseAuth.currentUser != null)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    suspend fun syncFromFirebaseSession(uid: String) {
        val fu = firebaseAuth.currentUser ?: return
        if (fu.uid != uid) return
        firestoreSync.pullAndMergeLocal(uid, fu)
    }

    suspend fun signInWithEmailPassword(email: String, password: String): Result<UserEntity> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val fu = firebaseAuth.currentUser!!
            firestoreSync.pullAndMergeLocal(fu.uid, fu)
            val entity = userDao.getCurrentUser().first()
                ?: return Result.failure(IllegalStateException("No se pudo cargar el perfil local."))
            Result.success(entity)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(IllegalArgumentException("Correo o contraseña incorrectos."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAccountWithEmailPassword(
        nombrePerfil: String,
        email: String,
        password: String
    ): Result<UserEntity> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            firebaseAuth.currentUser!!.updateProfile(
                UserProfileChangeRequest.Builder().setDisplayName(nombrePerfil.trim()).build()
            ).await()
            val fu = firebaseAuth.currentUser!!
            firestoreSync.pullAndMergeLocal(fu.uid, fu)
            val entity = userDao.getCurrentUser().first()
                ?: return Result.failure(IllegalStateException("No se pudo crear el perfil local."))
            val actualizado = entity.copy(name = nombrePerfil.trim())
            userDao.updateUser(actualizado)
            firestoreSync.pushUsuario(fu.uid, actualizado)
            Result.success(actualizado)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(IllegalStateException("Ya existe una cuenta con ese correo."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogleIdToken(idToken: String): Result<UserEntity> {
        return try {
            val cred = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(cred).await()
            val fu = firebaseAuth.currentUser!!
            firestoreSync.pullAndMergeLocal(fu.uid, fu)
            val entity = userDao.getCurrentUser().first()
                ?: return Result.failure(IllegalStateException("No se pudo cargar el perfil local."))
            firestoreSync.pushUsuario(fu.uid, entity)
            Result.success(entity)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Perfil local opcional sin Firebase (no desbloquea Chat/Dojo). */
    suspend fun createOrUpdateLocalUser(name: String, email: String): UserEntity {
        val existing = userDao.getCurrentUser().first()
        val baseXp = existing?.totalXp ?: 0
        val level = LevelSystem.levelForXp(baseXp)
        val rank = LevelSystem.rankForLevel(level)
        val entity = (existing ?: UserEntity(
            id = "local_${UUID.randomUUID()}",
            name = name,
            email = email,
            totalXp = 0,
            currentStreak = 0,
            totalMinutesCalm = 0,
            level = 1,
            rank = LevelSystem.rankForLevel(1),
            isActive = true
        )).copy(
            name = name,
            email = email,
            level = level,
            rank = rank,
            isActive = true
        )
        userDao.deactivateAll()
        userDao.insertUser(entity)
        return entity
    }

    suspend fun updateAvatar(avatarUri: String?) {
        val user = userDao.getCurrentUser().first() ?: return
        userDao.updateUser(user.copy(avatarUrl = avatarUri))
        val uid = firebaseAuth.currentUser?.uid ?: return
        firestoreSync.pushUsuario(uid, user.copy(avatarUrl = avatarUri))
    }

    suspend fun hasActiveSession(): Boolean = firebaseAuth.currentUser != null

    suspend fun logout() {
        runCatching { googleSignInHelper.signOut() }
        firebaseAuth.signOut()
        userDao.deactivateAll()
    }

    suspend fun updateRoomUser(entity: UserEntity) {
        userDao.deactivateAll()
        userDao.insertUser(entity)
    }

    suspend fun replaceCurrentUser(entity: UserEntity) {
        userDao.deactivateAll()
        userDao.insertUser(entity)
        firebaseAuth.currentUser?.uid?.let { firestoreSync.pushUsuario(it, entity) }
    }

    fun firebaseUid(): String? = firebaseAuth.currentUser?.uid

    suspend fun updateStreak(userId: String) {
        val user = userDao.getUserById(userId) ?: return
        val prefs = appContext.getSharedPreferences("user_streaks", Context.MODE_PRIVATE)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val lastActive = prefs.getString("last_active_day_$userId", "")

        if (lastActive != today) {
            val yesterday = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(
                java.util.Date(System.currentTimeMillis() - 86400000)
            )
            val newStreak = if (lastActive == yesterday) {
                user.currentStreak + 1
            } else if (lastActive.isNullOrEmpty()) {
                // If it's their very first interaction or streak system init
                if (user.currentStreak > 0) user.currentStreak else 1
            } else {
                1 // reset streak to 1 day if they skipped a day
            }

            userDao.updateStreak(userId, newStreak)
            prefs.edit().putString("last_active_day_$userId", today).apply()

            // Sync with Firebase
            val fu = firebaseAuth.currentUser
            if (fu != null && fu.uid == userId) {
                firestoreSync.pushUsuario(fu.uid, user.copy(currentStreak = newStreak))
            }
        }
    }

    suspend fun addCalmMinutes(userId: String, minutes: Int) {
        val user = userDao.getUserById(userId) ?: return
        val newCalm = user.totalMinutesCalm + minutes
        userDao.addCalmMinutes(userId, minutes)

        // Sync with Firebase
        val fu = firebaseAuth.currentUser
        if (fu != null && fu.uid == userId) {
            firestoreSync.pushUsuario(fu.uid, user.copy(totalMinutesCalm = newCalm))
        }
    }
}
