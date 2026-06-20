package com.example.scanlink.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.scanlink.features.authentication.presentation.login.LoginScreen
import com.example.scanlink.features.authentication.presentation.register.RegisterScreen
import com.example.scanlink.features.authentication.presentation.register.RegisterViewModel

fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    registerViewModel: RegisterViewModel
) {
    navigation(
        startDestination = Routes.LOGIN,
        route = Routes.AUTH_GRAPH
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
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
