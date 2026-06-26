package com.example.scanlink.features.document_scanner.presentation.camera

import android.graphics.Bitmap

sealed class CameraUiState {
    object Initial : CameraUiState()
    object Capturing : CameraUiState()
    data class Success(val imageUri: String, val mode: String = "Quét") : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}

