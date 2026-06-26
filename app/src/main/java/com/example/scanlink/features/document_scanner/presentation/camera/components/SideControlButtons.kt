package com.example.scanlink.features.document_scanner.presentation.camera.components
import  androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SideControlButtons(rightSide: Boolean = false) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!rightSide) {
            // Left side
            ControlButton(icon = Icons.Default.FlashOn, isActive = true)
            ControlButton(icon = Icons.Default.GridOn)
        } else {
            // Right side
            ControlButton(icon = Icons.Default.FlipCameraAndroid)
            GalleryButton()
        }
    }
}

@Composable
fun ControlButton(
    icon: ImageVector,
    isActive: Boolean = false,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1C1C24))
            .border(
                width = 1.dp,
                color = if (isActive)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                else
                    Color(0xFF252532),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isActive)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun GalleryButton() {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1C1C24))
            .border(1.dp, Color(0xFF252532), RoundedCornerShape(14.dp))
            .clickable { /* Open gallery */ },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.PhotoLibrary,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )

    }
}