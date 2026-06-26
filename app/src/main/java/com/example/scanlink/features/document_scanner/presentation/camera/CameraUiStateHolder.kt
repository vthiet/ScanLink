package com.example.scanlink.features.document_scanner.presentation.camera

import android.graphics.Bitmap

data class CameraUiStateHolder(
    val selectedMode: String = "Quét",
    val flashEnabled: Boolean = false,
    val isFrontCamera: Boolean = false,
    val uiState: CameraUiState = CameraUiState.Initial,
    val isLoading: Boolean = false,

    val processedBitmap: Bitmap? = null,
    val detectedText: String = "",
    val pdfPath: String? = null,
    val capturedImageUri: String? = null

)