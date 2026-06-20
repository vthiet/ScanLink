package com.example.scanlink.features.document_scanner.presentation.camera.components

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CameraBottomControls(
    selectedMode: String,
    onModeSelected: (String) -> Unit,
    imageCapture: ImageCapture?,
    flashEnabled: Boolean,
    onFlashToggle: () -> Unit,
    onPhotoCaptured: (String) -> Unit,
    isLoading: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A0A0C))
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        ModeSelector(selectedMode, onModeSelected)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlButton(
                icon = Icons.Default.FlashOn,
                isActive = flashEnabled,
                onClick = onFlashToggle
            )

            CaptureButton(
                imageCapture = imageCapture,
                onPhotoCaptured = onPhotoCaptured,
                isLoading = isLoading
            )

            GalleryButton()
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Giữ điện thoại thẳng · Tự động căn chỉnh góc méo",
            color = Color(0xFF555555),
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}