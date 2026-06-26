package com.example.scanlink.core.ui.components.bottom_bar.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String


)
val items = listOf(
    BottomNavItem("Home", Icons.Default.Home, "home"),
    BottomNavItem("Transfer", Icons.Default.SwapHoriz, "transfer"),
    BottomNavItem("History", Icons.Default.History, "history"),
    BottomNavItem("Profile", Icons.Default.AccountCircle, "profile")
)
