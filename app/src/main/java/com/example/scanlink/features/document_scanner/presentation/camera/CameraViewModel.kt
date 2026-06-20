package com.example.scanlink.features.document_scanner.presentation.camera

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CameraViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiStateHolder())
    val uiState = _uiState.asStateFlow()

    fun onModeSelected(mode: String) {
        _uiState.value = _uiState.value.copy(selectedMode = mode)
    }

    fun toggleFlash() {
        _uiState.value = _uiState.value.copy(flashEnabled = !_uiState.value.flashEnabled)
    }

    fun switchCamera() {
        _uiState.value = _uiState.value.copy(isFrontCamera = !_uiState.value.isFrontCamera)
    }

    fun onCaptureStarted() {
        _uiState.value = _uiState.value.copy(
            uiState = CameraUiState.Capturing,
            isLoading = true
        )
    }

    fun onCaptureSuccess(imageUri: String) {
        _uiState.value = _uiState.value.copy(
            uiState = CameraUiState.Success(imageUri, _uiState.value.selectedMode),
            isLoading = false
        )
    }

    fun onCaptureError(message: String) {
        _uiState.value = _uiState.value.copy(
            uiState = CameraUiState.Error(message),
            isLoading = false
        )
    }
}