package com.example.scanlink.features.authentication.presentation.login

import com.example.scanlink.features.authentication.domain.entities.UserEntity

data class LoginState(
    val emailInput: String = "",
    val emailErrorResId: Int? = null,

    val passwordInput: String = "",
    val passwordErrorResId: Int? = null,

    val isLoading: Boolean = false,
    val errorResId: Int? = null,
    val successUser: UserEntity? = null
)
