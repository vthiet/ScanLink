package com.example.scanlink.features.authentication.presentation.login

sealed class LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent()
    data class PasswordChanged(val value: String) : LoginEvent()
    object Submit : LoginEvent()
    data class GoogleSignInResult(val idToken: String) : LoginEvent()
    data class GoogleSignInFailed(val exception: Throwable) : LoginEvent()
}
