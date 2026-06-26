package com.example.scanlink.features.document_scanner.presentation.camera

import android.graphics.Bitmap

sealed class CameraUiState {
    object Initial : CameraUiState()
    object Capturing : CameraUiState()
    object Transforming : CameraUiState()
    object Filtering : CameraUiState()
    object OcrProcessing : CameraUiState()
    
    data class Success(val imageUri: String, val mode: String = "Quét") : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}

enum class ScanFilterType {
    ORIGINAL, B_W, GRAYSCALE, MAGIC_COLOR
}

data class CameraUiStateHolder(
    val selectedMode: String = "Quét",
    val flashEnabled: Boolean = false,
    val isFrontCamera: Boolean = false,
    val uiState: CameraUiState = CameraUiState.Initial,
    val isLoading: Boolean = false,

    val originalBitmap: Bitmap? = null,
    val transformedBitmap: Bitmap? = null,
    val processedBitmap: Bitmap? = null,
    val selectedFilter: ScanFilterType = ScanFilterType.B_W,
    val detectedText: String = "",
    val pdfPath: String? = null,
    val capturedImageUri: String? = null
)
