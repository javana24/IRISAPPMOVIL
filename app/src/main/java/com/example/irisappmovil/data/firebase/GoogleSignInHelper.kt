package com.example.irisappmovil.data.firebase

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.activity.ComponentActivity
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.iris.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSignInHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun getGoogleIdTokenForFirebase(activity: ComponentActivity): Result<String> {
        val credentialManager = CredentialManager.create(context)
        val serverClientId = context.getString(R.string.default_web_client_id)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        return try {
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val googleId = GoogleIdTokenCredential.createFrom(credential.data)
                val token = googleId.idToken
                if (token.isNullOrBlank()) {
                    Result.failure(IllegalStateException("Token vacío."))
                } else {
                    Result.success(token)
                }
            } else {
                Result.failure(IllegalStateException("Credencial no reconocida."))
            }
        } catch (_: GetCredentialCancellationException) {
            Result.failure(GoogleSignInCancelledException())
        } catch (e: GetCredentialException) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        runCatching {
            val cm = CredentialManager.create(context)
            cm.clearCredentialState(ClearCredentialStateRequest())
        }
    }
}
