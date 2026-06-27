package com.example.scanlink.features.splash.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToMain: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SplashContent()

    LaunchedEffect(uiState.targetRoute) {
        when (uiState.targetRoute) {
            null -> Unit
            com.example.scanlink.navigation.Routes.MAIN_GRAPH -> onNavigateToMain()
            com.example.scanlink.navigation.Routes.AUTH_GRAPH -> onNavigateToAuth()
        }
    }
}
