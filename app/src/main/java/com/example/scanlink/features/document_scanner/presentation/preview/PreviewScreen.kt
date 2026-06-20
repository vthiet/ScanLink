package com.example.scanlink.features.document_scanner.presentation.preview

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun PreviewScreen(
    imageUri: String,
    onClose: () -> Unit,
    onSave: () -> Unit,
    onRetake: () -> Unit
) {
    PreviewContent(
        imageUri = imageUri,
        onClose = onClose,
        onSave = onSave,
        onRetake = onRetake
    )
}