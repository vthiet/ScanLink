package com.example.scanlink.features.document_scanner.presentation.camera

import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanlink.features.document_scanner.presentation.camera.components.CameraBottomControls
import com.example.scanlink.features.document_scanner.presentation.camera.components.CameraViewfinder
import com.example.scanlink.features.document_scanner.presentation.camera.components.ScanningOverlay
import com.example.scanlink.features.file_sharing.presentation.ui.camera.components.*

@Composable
fun CameraContent(
    onClose: () -> Unit = {},
    onPhotoCaptured: (String) -> Unit = {},
    viewModel: CameraViewModel
) {

    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val imageCapture = remember { mutableStateOf<ImageCapture?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Màn hình Camera chính
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
                    // Gọi onCaptureSuccess với context, uri và lambda điều hướng
                    viewModel.onCaptureSuccess(context, uri) {
                        onPhotoCaptured(uri)
                    }
                },
                isLoading = state.isLoading
            )
        }

        // Lớp phủ hiệu ứng quét
        if (state.isLoading) {
            ScanningOverlay(
                bitmap = state.processedBitmap ?: state.originalBitmap,
                state = state.uiState
            )
        }
    }
}
