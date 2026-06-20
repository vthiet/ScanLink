package com.example.scanlink.core.ui.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItemData(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val color: Color,
    val badge: String? = null,
    val isToggle: Boolean = false,
    val toggleState: Boolean = false
)