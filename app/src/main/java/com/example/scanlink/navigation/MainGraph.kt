package com.example.scanlink.navigation


import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.scanlink.core.ui.components.bottom_bar.AppBottomBar


@Composable
fun MainGraph(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val shouldShowBottomBar =
        NavigationConfig.shouldShowBottomBar(currentRoute)

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route)
                    },
                    onCameraClick = {
                        navController.navigate("camera")
                    }
                )
            }
        }
    ) { padding ->

        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(padding),
            onLogout = onLogout
        )
    }
}

