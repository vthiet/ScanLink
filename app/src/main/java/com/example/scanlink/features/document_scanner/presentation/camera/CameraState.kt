package com.example.scanlink.features.file_sharing.presentation.scan

sealed class CameraUiState {
    object Initial : CameraUiState()
    object Capturing : CameraUiState()
    data class Success(val imageUri: String, val mode: String = "Quét") : CameraUiState()
    data class Error(val message: String) : CameraUiState()
}

data class CameraUiStateHolder(
    val selectedMode: String = "Quét",
    val flashEnabled: Boolean = false,
    val isFrontCamera: Boolean = false,
    val uiState: CameraUiState = CameraUiState.Initial,
    val isLoading: Boolean = false
)