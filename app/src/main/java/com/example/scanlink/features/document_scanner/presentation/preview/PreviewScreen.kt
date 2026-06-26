package com.example.scanlink.features.document_scanner.presentation.preview

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun PreviewScreen(
    imageUri: String,
    onClose: () -> Unit,
    onRetake: () -> Unit,
    onCrop: () -> Unit,
    onRotate: () -> Unit,
    onExtractText: () -> Unit,
    onDone: () -> Unit,
    viewModel: PreviewViewModel = viewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(imageUri) {
        viewModel.setImageUri(imageUri)
    }

    LaunchedEffect(state.savedUri) {
        if (state.savedUri != null) {
            Toast.makeText(context, "Đã lưu tài liệu", Toast.LENGTH_SHORT).show()
            onDone()
        }
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    PreviewContent(
        state = state,
        onClose = onClose,
        onRetake = onRetake,
        onRotate = {
            viewModel.rotateRight()
            onRotate()
        },
        onCrop = {
            viewModel.toggleCropMode()
        },
        onCropRectChange = viewModel::updateCropRect,
        onApplyCrop = {
            viewModel.applyCrop(context)
        },
        onExtractText = onExtractText,
        onDone = {
            viewModel.saveImage(context)
        }
    )
}
