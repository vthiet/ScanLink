package com.example.scanlink.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)
    object Transfer : BottomNavItem("transfer", "Transfer", Icons.Default.Sync)
    object Scan: BottomNavItem("scan","Scan",Icons.Default.DocumentScanner)
    object History : BottomNavItem("history", "History", Icons.Default.History)
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}