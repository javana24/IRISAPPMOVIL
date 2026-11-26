package com.example.irisappmovil.util

import android.util.Patterns

object EmailValidation {
    fun isValid(email: String): Boolean {
        val trimmed = email.trim()
        if (trimmed.isBlank()) return false
        return Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()
    }
}
