package com.example.scanlink.features.authentication.presentation.register

import com.example.scanlink.features.authentication.domain.entities.UserEntity

data class RegisterState(
    val emailInput: String = "",
    val emailErrorResId: Int? = null,

    val passwordInput: String = "",
    val passwordErrorResId: Int? = null,

    val confirmPasswordInput: String = "",
    val confirmPasswordErrorResId: Int? = null,

    val displayNameInput: String = "",
    val displayNameErrorResId: Int? = null,

    val dateOfBirthInput: String = "",
    val dateOfBirthErrorResId: Int? = null,

    val genderInput: String = "",
    val genderErrorResId: Int? = null,

    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,

    val isLoading: Boolean = false,
    val errorResId: Int? = null,
    val successUser: UserEntity? = null
)
