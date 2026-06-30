package com.example.scanlink.features.document_scanner.presentation.camera.components

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun CameraBottomControls(
    selectedMode: String,
    onModeSelected: (String) -> Unit,
    imageCapture: ImageCapture?,
    flashEnabled: Boolean,
    onFlashToggle: () -> Unit,
    onPhotoCaptured: (String) -> Unit,
    capturedImages: List<String> = emptyList(),
    onThumbnailClick: () -> Unit = {},
    isLoading: Boolean = false
) {
    val capturedCount = capturedImages.size

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        ModeSelector(selectedMode, onModeSelected)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BatchThumbnail(
                imageUri = capturedImages.lastOrNull(),
                count = capturedCount,
                onClick = onThumbnailClick
            )

            CaptureButton(
                imageCapture = imageCapture,
                onPhotoCaptured = onPhotoCaptured,
                isLoading = isLoading
            )

            ControlButton(
                icon = Icons.Default.FlashOn,
                isActive = flashEnabled,
                onClick = onFlashToggle
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Giữ điện thoại thẳng · Tự động căn chỉnh góc méo",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun BatchThumbnail(
    imageUri: String?,
    count: Int,
    onClick: () -> Unit
) {
    BadgedBox(
        badge = {
            if (count > 0) {
                Badge(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(text = count.toString())
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                .clickable(enabled = imageUri != null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Latest captured page",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
    }
}
