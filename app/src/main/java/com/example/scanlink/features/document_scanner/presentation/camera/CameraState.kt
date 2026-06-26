package com.example.scanlink.features.document_scanner.presentation.camera

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

