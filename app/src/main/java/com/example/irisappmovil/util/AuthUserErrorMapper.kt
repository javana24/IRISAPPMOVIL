package com.example.irisappmovil.util

import android.content.Context
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.irisappmovil.data.firebase.GoogleSignInCancelledException
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.ConnectionResult
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.iris.app.R

object AuthUserErrorMapper {

    private const val SIGN_IN_CANCELLED_CODE = 12501

    fun shouldShowMessage(throwable: Throwable?): Boolean {
        if (throwable == null) return false
        if (throwable is GoogleSignInCancelledException) return false
        if (throwable is GetCredentialCancellationException) return false
        if (throwable is ApiException) {
            val code = throwable.statusCode
            return code != SIGN_IN_CANCELLED_CODE && code != ConnectionResult.CANCELED
        }
        return true
    }

    fun messageFor(context: Context, throwable: Throwable?): String? {
        if (throwable == null || !shouldShowMessage(throwable)) return null
        return when (throwable) {
            is ApiException -> when (throwable.statusCode) {
                SIGN_IN_CANCELLED_CODE, ConnectionResult.CANCELED -> null
                else -> context.getString(R.string.auth_error_google_generic)
            }
            is FirebaseAuthInvalidCredentialsException ->
                context.getString(R.string.auth_error_credentials)
            is FirebaseAuthUserCollisionException ->
                context.getString(R.string.auth_error_email_in_use)
            is FirebaseNetworkException ->
                context.getString(R.string.auth_error_network)
            is GetCredentialException -> context.getString(R.string.auth_error_google_generic)
            else -> throwable.message?.takeIf { m ->
                m.isNotBlank() &&
                    !m.matches(Regex("^\\d+$")) &&
                    !m.contains("12501") &&
                    !m.contains("DEVELOPER_ERROR")
            } ?: context.getString(R.string.auth_error_generic)
        }
    }
}
