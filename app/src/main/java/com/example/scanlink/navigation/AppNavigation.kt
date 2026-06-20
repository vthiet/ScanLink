package com.example.scanlink.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.scanlink.features.authentication.presentation.register.RegisterViewModel
import com.example.scanlink.features.splash.presentation.SplashScreen

@Composable
fun AppNavigation(
    registerViewModel: RegisterViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(Routes.AUTH_GRAPH) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToMain = {
                    navController.navigate(Routes.MAIN_GRAPH) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        authGraph(navController, registerViewModel)

        composable(Routes.MAIN_GRAPH) {
            MainGraph(
                onLogout = {
                    navController.navigate(Routes.AUTH_GRAPH) {
                        popUpTo(Routes.MAIN_GRAPH) { inclusive = true }
                    }
                }
            )
        }
    }
}
