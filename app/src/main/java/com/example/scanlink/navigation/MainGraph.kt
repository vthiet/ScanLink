package com.example.scanlink.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.scanlink.features.dashboard.presentation.home.HomeScreen
import com.example.scanlink.features.dashboard.presentation.profile.ProfileScreen
import com.example.scanlink.features.document_scanner.presentation.transfer.TransferScreen
import com.example.scanlink.features.file_sharing.presentation.history.HistoryScreen
import com.example.scanlink.features.file_sharing.presentation.scan.ScanScreen

@Composable
fun MainGraph(
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        },
        modifier = modifier
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(BottomNavItem.Home.route) { HomeScreen() }
            composable(BottomNavItem.Transfer.route) { TransferScreen() }
            composable(BottomNavItem.Scan.route) { ScanScreen() }
            composable(BottomNavItem.History.route) { HistoryScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Transfer,
        BottomNavItem.History,
        BottomNavItem.Profile
    )
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    Box {
        NavigationBar {
            items.take(2).forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(BottomNavItem.Home.route)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) }
                )
            }
            NavigationBarItem(
                selected = false,
                onClick = {},
                icon = { Spacer(modifier = Modifier.size(48.dp)) },
                label = { Text("") },
                enabled = false
            )
            items.drop(2).forEach { item ->
                NavigationBarItem(
                    selected = currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            popUpTo(BottomNavItem.Home.route)
                            launchSingleTop = true
                        }
                    },
                    icon = { Icon(item.icon, contentDescription = item.label) },
                    label = { Text(item.label) }
                )
            }
        }

        FloatingActionButton(
            onClick = {
                navController.navigate(BottomNavItem.Scan.route) {
                    popUpTo(BottomNavItem.Home.route)
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-25).dp)
                .size(64.dp),
            containerColor = Color(0xFF00C853),
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Scan",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}
