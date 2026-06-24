package com.example.scanlink.features.document_scanner.presentation.preview

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.document_scanner.data.image.PreviewImageProcessor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PreviewViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    fun setImageUri(uri: String) {
        _uiState.update { it.copy(imageUri = uri, errorMessage = null) }
    }

    fun rotateRight() {
        _uiState.update { it.copy(rotation = normalizeRotation(it.rotation + 90f)) }
    }
    fun rotate() {
        _uiState.update {
            it.copy(rotation = (it.rotation + 90f) % 360f)
        }
    }

    fun toggleCropMode() {
        _uiState.update {
            it.copy(
                cropMode = !it.cropMode,
                rotation = 0f,
                flipHorizontal = false,
                flipVertical = false
            )
        }
    }

    fun saveImage(context: Context, onSaved: (Uri) -> Unit = {}) {
        val state = _uiState.value
        if (state.imageUri.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                val original = PreviewImageProcessor.loadBitmap(
                    context = context,
                    uri = Uri.parse(state.imageUri)
                )

                val transformed = PreviewImageProcessor.transform(
                    bitmap = original,
                    rotation = if (state.cropMode) 0f else state.rotation,
                    flipHorizontal = state.flipHorizontal,
                    flipVertical = state.flipVertical,
                    cropCenter = state.cropMode
                )

                PreviewImageProcessor.saveToPictures(
                    context = context,
                    bitmap = transformed,
                    fileName = "ScanLink_${System.currentTimeMillis()}"
                )
            }.onSuccess { uri ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        savedUri = uri.toString()
                    )
                }
                onSaved(uri)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.localizedMessage ?: "Không thể lưu ảnh."
                    )
                }
            }
        }
    }

    private fun normalizeRotation(value: Float): Float {
        val result = value % 360f
        return if (result < 0f) result + 360f else result
    }

    fun updateCropRect(cropRect: CropRect) {
        _uiState.update {
            it.copy(cropRect = cropRect)
        }
    }

    fun applyCrop(context: Context) {
        val state = _uiState.value
        if (state.imageUri.isBlank()) return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                val original = PreviewImageProcessor.loadBitmap(
                    context = context,
                    uri = Uri.parse(state.imageUri)
                )

                val cropped = PreviewImageProcessor.cropByRect(
                    bitmap = original,
                    cropRect = state.cropRect
                )

                PreviewImageProcessor.saveToPictures(
                    context = context,
                    bitmap = cropped,
                    fileName = "ScanLink_crop_${System.currentTimeMillis()}"
                )
            }.onSuccess { uri ->
                _uiState.update {
                    it.copy(
                        imageUri = uri.toString(),
                        rotation = 0f,
                        flipHorizontal = false,
                        flipVertical = false,
                        cropMode = false,
                        cropRect = CropRect(),
                        isSaving = false
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.localizedMessage ?: "Không thể cắt ảnh."
                    )
                }
            }
        }
    }
}
