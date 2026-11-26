package com.example.irisappmovil.ui.Auth

/**
 * Fortaleza de contraseña para registro (orientativo; el servidor puede exigir más).
 */
object PasswordStrength {
    const val WEAK = 0
    const val FAIR = 1
    const val GOOD = 2
    const val STRONG = 3

    fun score(password: String): Int {
        if (password.isEmpty()) return WEAK
        var criteria = 0
        if (password.length >= 8) criteria++
        if (password.length >= 12) criteria++
        if (password.any { it.isUpperCase() }) criteria++
        if (password.any { it.isLowerCase() }) criteria++
        if (password.any { it.isDigit() }) criteria++
        if (password.any { !it.isLetterOrDigit() }) criteria++
        return when {
            criteria <= 2 -> WEAK
            criteria <= 4 -> FAIR
            criteria <= 5 -> GOOD
            else -> STRONG
        }
    }

    fun isAcceptableForRegister(password: String): Boolean =
        password.length >= 8 &&
            password.any { it.isDigit() } &&
            password.any { it.isLetter() }
}
