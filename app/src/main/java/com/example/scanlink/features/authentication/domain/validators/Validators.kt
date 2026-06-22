package com.example.scanlink.features.authentication.domain.validators

object EmailValidator {
    fun isValid(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }
}

object PasswordValidator {
    fun isValid(password: String): Boolean {
        // At least 6 characters, minimum requirement for Firebase
        return password.length >= 6
    }

    fun getErrorMessage(password: String): String? {
        return when {
            password.isEmpty() -> "Password is required"
            password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }
}

