package com.example.scanlink.features.document_scanner.presentation.preview

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.document_scanner.data.engine.ScanEngine
import com.example.scanlink.features.document_scanner.data.image.PreviewImageProcessor
import com.example.scanlink.features.document_scanner.presentation.camera.ScanFilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PreviewViewModel @Inject constructor(
    private val scanEngine: ScanEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    private var originalBitmap: Bitmap? = null

    fun setImageUri(context: Context, uri: String) {
        if (_uiState.value.imageUri == uri) return

        _uiState.update { it.copy(imageUri = uri, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val bitmap = PreviewImageProcessor.loadBitmap(
                    context = context,
                    uri = Uri.parse(uri)
                )
                originalBitmap = bitmap
                applyFilterInternal(_uiState.value.selectedFilter)
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.localizedMessage) }
            }
        }
    }

    fun onFilterSelected(filterType: ScanFilterType) {
        _uiState.update { it.copy(selectedFilter = filterType) }
        applyFilterInternal(filterType)
    }

    private fun applyFilterInternal(filterType: ScanFilterType) {
        val bitmap = originalBitmap ?: return
        viewModelScope.launch(Dispatchers.Default) {
            val filtered = scanEngine.applyFilters(bitmap, filterType)
            _uiState.update { it.copy(previewBitmap = filtered) }
        }
    }

    fun rotateRight() {
        _uiState.update { it.copy(rotation = normalizeRotation(it.rotation + 90f)) }
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

    fun saveImage(context: Context, onSaved: (String) -> Unit = {}) {
        val state = _uiState.value
        val bitmapToSave = state.previewBitmap ?: originalBitmap ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                val transformed = PreviewImageProcessor.transform(
                    bitmap = bitmapToSave,
                    rotation = state.rotation,
                    flipHorizontal = state.flipHorizontal,
                    flipVertical = state.flipVertical,
                    cropCenter = false
                )

                val uri = PreviewImageProcessor.saveToPictures(
                    context = context,
                    bitmap = transformed,
                    fileName = "ScanLink_Filtered_${System.currentTimeMillis()}"
                )
                uri.toString()
            }.onSuccess { savedUri ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        savedUri = savedUri
                    )
                }
                withContext(Dispatchers.Main) {
                    onSaved(savedUri)
                }
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
        val bitmapToCrop = originalBitmap ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                val cropped = PreviewImageProcessor.cropByRect(
                    bitmap = bitmapToCrop,
                    cropRect = state.cropRect
                )
                originalBitmap = cropped

                val uri = PreviewImageProcessor.saveToPictures(
                    context = context,
                    bitmap = cropped,
                    fileName = "ScanLink_crop_${System.currentTimeMillis()}"
                )
                uri.toString()
            }.onSuccess { uriString ->
                _uiState.update {
                    it.copy(
                        imageUri = uriString,
                        rotation = 0f,
                        flipHorizontal = false,
                        flipVertical = false,
                        cropMode = false,
                        cropRect = CropRect(),
                        isSaving = false
                    )
                }
                applyFilterInternal(state.selectedFilter)
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
