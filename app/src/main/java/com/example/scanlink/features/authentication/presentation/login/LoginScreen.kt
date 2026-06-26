package com.example.scanlink.features.authentication.presentation.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.successUser) {
        if (state.successUser != null) {
            onLoginSuccess()
        }
    }

    LoginContent(
        viewModel = viewModel,
        state = state,
        onNavigateToRegister = onNavigateToRegister
    )
}
