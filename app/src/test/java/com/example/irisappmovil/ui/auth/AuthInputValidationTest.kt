package com.example.irisappmovil.ui.auth

import com.example.irisappmovil.ui.Auth.PasswordStrength
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests mínimos de validación de registro (SCRUM-78). Sin Android ni Firebase.
 */
class AuthInputValidationTest {

    @Test
    fun isAcceptableForRegister_acceptsMinLengthWithLetterAndDigit() {
        assertTrue(PasswordStrength.isAcceptableForRegister("iris2026"))
    }

    @Test
    fun isAcceptableForRegister_rejectsTooShortOrMissingClasses() {
        assertFalse(PasswordStrength.isAcceptableForRegister("iris"))
        assertFalse(PasswordStrength.isAcceptableForRegister("12345678"))
        assertFalse(PasswordStrength.isAcceptableForRegister("abcdefgh"))
    }

    @Test
    fun score_strongerWithMoreCriteria() {
        assertTrue(PasswordStrength.score("Iris2026!") >= PasswordStrength.score("iris2026"))
    }
}
