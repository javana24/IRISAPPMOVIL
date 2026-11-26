package com.example.irisappmovil.data.firebase

import com.example.irisappmovil.data.api.UserRepository
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Singleton
class FirebaseSessionCoordinator @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val listener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser ?: return@AuthStateListener
        scope.launch {
            userRepository.syncFromFirebaseSession(user.uid)
        }
    }

    fun startListening() {
        firebaseAuth.addAuthStateListener(listener)
    }
}
