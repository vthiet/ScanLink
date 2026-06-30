package com.example.scanlink.features.document_scanner.presentation.preview

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanlink.features.document_scanner.presentation.camera.CameraUiState

@Composable
fun PreviewScreen(
    imageUri: String,
    cameraUiState: CameraUiState = CameraUiState.Initial,
    onClose: () -> Unit,
    onRetake: () -> Unit,
    onCrop: () -> Unit,
    onRotate: () -> Unit,
    onExtractText: (String) -> Unit,
    onDone: (String) -> Unit,
    viewModel: PreviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(imageUri) {
        viewModel.setImageUri(imageUri)
    }

    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        }
    }

    PreviewContent(
        state = state,
        cameraUiState = cameraUiState,
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
            // applyCrop() không còn nhận tham số context
            viewModel.applyCrop()
        },
        onExtractText = {
            // saveImage() nhận callback trực tiếp, không nhận context
            viewModel.saveImage { savedUri ->
                onExtractText(savedUri)
            }
        },
        onDone = {
            // saveImage() nhận callback trực tiếp, không nhận context
            viewModel.saveImage { savedUri ->
                onDone(savedUri)
            }
        },
        onFilterSelected = viewModel::onFilterSelected
    )
}
