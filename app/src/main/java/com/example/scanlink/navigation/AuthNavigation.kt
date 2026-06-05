package com.example.scanlink.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scanlink.features.authentication.presentation.viewmodels.AuthState
import com.example.scanlink.features.authentication.presentation.viewmodels.AuthViewModel
import com.example.scanlink.features.authentication.presentation.ui.LoginScreen
import com.example.scanlink.features.authentication.presentation.ui.RegisterScreen

sealed class AuthRoute(val route: String) {
    data object Login : AuthRoute("login")
    data object Register : AuthRoute("register")
}

@Composable
fun AuthNavHost(
    authViewModel: AuthViewModel,
    onAuthSuccess: () -> Unit
) {
    val navController = rememberNavController()
    val uiState by authViewModel.uiState.collectAsState()

    // If user is authenticated, return to main screen
    if (uiState.authState is AuthState.Authenticated) {
        onAuthSuccess()
        return
    }

    NavHost(
        navController = navController,
        startDestination = AuthRoute.Login.route
    ) {
        composable(AuthRoute.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(AuthRoute.Register.route) {
                        popUpTo(AuthRoute.Login.route) { saveState = true }
                    }
                },
                onLoginSuccess = onAuthSuccess
            )
        }
        composable(AuthRoute.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(AuthRoute.Login.route) {
                        popUpTo(AuthRoute.Register.route) { inclusive = true }
                    }
                },
                onRegisterSuccess = onAuthSuccess
            )
        }
    }
}

