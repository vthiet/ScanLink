package com.example.scanlink.features.document_scanner.presentation.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.document_scanner.presentation.preview.components.*

@Composable
fun PreviewContent(
    imageUri: String,
    onClose: () -> Unit,
    onSave: () -> Unit,
    onRetake: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0C))
    ) {

        PreviewTopBar(
            onClose = onClose
        )

        PreviewImageViewer(
            imageUri = imageUri,
            modifier = Modifier.weight(1f)
        )

        PreviewBottomActions(
            onRetake = onRetake,
            onSave = onSave
        )
    }
}