package com.example.scanlink.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.scanlink.features.authentication.presentation.login.LoginScreen
import com.example.scanlink.features.authentication.presentation.login.LoginViewModel
import com.example.scanlink.features.authentication.presentation.register.RegisterScreen
import com.example.scanlink.features.authentication.presentation.register.RegisterViewModel

fun NavGraphBuilder.authGraph(
    navController: NavHostController
) {
    navigation(
        startDestination = Routes.LOGIN,
        route = Routes.AUTH_GRAPH
    ) {
        composable(Routes.LOGIN) {
            val loginViewModel: LoginViewModel = hiltViewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN_GRAPH) {
                        popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.REGISTER) {
            val registerViewModel: RegisterViewModel = hiltViewModel()
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Routes.MAIN_GRAPH) {
                        popUpTo(Routes.AUTH_GRAPH) { inclusive = true }
                    }
                }
            )
        }
    }
}
