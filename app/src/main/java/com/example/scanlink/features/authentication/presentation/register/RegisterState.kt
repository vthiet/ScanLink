package com.example.scanlink.features.authentication.presentation.register

import com.example.scanlink.features.authentication.domain.entities.UserEntity

data class RegisterState(
        val emailInput: String = "",
        val emailError: String? = null,
        val passwordInput: String = "",
        val passwordError: String? = null,
        val confirmPasswordInput: String = "",
        val confirmPasswordError: String? = null,
        val displayNameInput: String = "",
        val displayNameError: String? = null,
        val dateOfBirthInput: String = "",
        val dateOfBirthError: String? = null,
        val genderInput: String = "",
        val genderError: String? = null,
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        val isLoading: Boolean = false,
        val error: String? = null,
        val successUser: UserEntity? = null
)
