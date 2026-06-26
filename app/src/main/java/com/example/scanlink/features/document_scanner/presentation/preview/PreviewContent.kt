package com.example.scanlink.features.document_scanner.presentation.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.document_scanner.presentation.preview.components.PreviewBottomActions
import com.example.scanlink.features.document_scanner.presentation.preview.components.PreviewImageViewer
import com.example.scanlink.features.document_scanner.presentation.preview.components.PreviewTopBar

@Composable
fun PreviewContent(
    state: PreviewUiState,
    onClose: () -> Unit,
    onRetake: () -> Unit,
    onRotate: () -> Unit,
    onCrop: () -> Unit,
    onExtractText: () -> Unit,
    onDone: () -> Unit,
    onCropRectChange: (CropRect) -> Unit,
    onApplyCrop: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PreviewTopBar(
                title = if (state.cropMode) "Crop" else "Preview",
                cropMode = state.cropMode,
                onBack = onClose,
                onDone = {
                    if (state.cropMode) onApplyCrop() else onDone()
                }
            )

            PreviewImageViewer(
                imageUri = state.imageUri,
                rotation = state.rotation,
                flipHorizontal = state.flipHorizontal,
                flipVertical = state.flipVertical,
                cropMode = state.cropMode,
                cropRect = state.cropRect,
                onCropRectChange = onCropRectChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            )

            PreviewBottomActions(
                cropMode = state.cropMode,
                onRetake = onRetake,
                onRotate = onRotate,
                onCrop = onCrop,
                onExtractText = onExtractText,
                onDone = onDone,
                onApplyCrop = onApplyCrop
            )
        }

        if (state.isSaving) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}