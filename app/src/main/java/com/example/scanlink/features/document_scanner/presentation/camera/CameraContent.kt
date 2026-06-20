package com.example.scanlink.features.document_scanner.presentation.camera

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scanlink.features.document_scanner.presentation.camera.components.CameraBottomControls
import com.example.scanlink.features.document_scanner.presentation.camera.components.CameraViewfinder
import com.example.scanlink.features.file_sharing.presentation.ui.camera.components.*

@Composable
fun CameraContent(
    onClose: () -> Unit = {},
    onPhotoCaptured: (String) -> Unit = {},
    viewModel: CameraViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0A0C))
        ) {
            CameraHeader(
                onClose = onClose,
                onSwitchCamera = { viewModel.switchCamera() }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF111116)),
                contentAlignment = Alignment.Center
            ) {
                CameraViewfinder(
                    onImageCaptureReady = { imageCapture.value = it },
                    isFrontCamera = state.isFrontCamera
                )
            }

            CameraBottomControls(
                selectedMode = state.selectedMode,
                onModeSelected = { viewModel.onModeSelected(it) },
                imageCapture = imageCapture.value,
                flashEnabled = state.flashEnabled,
                onFlashToggle = { viewModel.toggleFlash() },
                onPhotoCaptured = { uri ->
                    viewModel.onCaptureSuccess(uri)
                    onPhotoCaptured(uri)
                },
                isLoading = state.isLoading
            )
        }

        // Loading Overlay
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF00CFA4))
            }
        }
    }
}