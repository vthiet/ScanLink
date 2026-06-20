package com.example.scanlink.features.authentication.presentation.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    // State Observation, draw ui each when State changed
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Logged in
    LaunchedEffect(state.successUser) {
        if (state.successUser != null) {
            onRegisterSuccess()
        }
    }

    RegisterContent(
        viewModel = viewModel,
        state = state,
        onNavigateToLogin = onNavigateToLogin
    )
}
