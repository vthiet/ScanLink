package com.example.scanlink.features.document_scanner.presentation.preview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.document_scanner.domain.entities.CropRect
import com.example.scanlink.features.document_scanner.domain.entities.ScanFilterType
import com.example.scanlink.features.document_scanner.domain.usecases.ApplyScanFilterUseCase
import com.example.scanlink.features.document_scanner.domain.usecases.CropPreviewImageUseCase
import com.example.scanlink.features.document_scanner.domain.usecases.LoadPreviewBitmapUseCase
import com.example.scanlink.features.document_scanner.domain.usecases.SavePreviewImageUseCase
import com.example.scanlink.features.document_scanner.domain.usecases.TransformPreviewImageUseCase
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
    private val loadPreviewBitmapUseCase: LoadPreviewBitmapUseCase,
    private val applyScanFilterUseCase: ApplyScanFilterUseCase,
    private val transformPreviewImageUseCase: TransformPreviewImageUseCase,
    private val savePreviewImageUseCase: SavePreviewImageUseCase,
    private val cropPreviewImageUseCase: CropPreviewImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreviewUiState())
    val uiState: StateFlow<PreviewUiState> = _uiState.asStateFlow()

    private var originalImage: Any? = null

    fun setImageUri(uri: String) {
        if (_uiState.value.imageUri == uri) return

        _uiState.update { it.copy(imageUri = uri, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val image = loadPreviewBitmapUseCase(uri)
                originalImage = image
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
        val image = originalImage ?: return
        viewModelScope.launch(Dispatchers.Default) {
            val filtered = applyScanFilterUseCase(image, filterType)
            _uiState.update { 
                it.copy(
                    previewBitmap = filtered as? android.graphics.Bitmap
                ) 
            }
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

    fun saveImage(onSaved: (String) -> Unit = {}) {
        val state = _uiState.value
        val imageToSave = originalImage ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                // Áp dụng bộ lọc trước khi lưu
                val filteredImage = applyScanFilterUseCase(imageToSave, state.selectedFilter)
                
                val transformed = transformPreviewImageUseCase(
                    image = filteredImage,
                    rotation = state.rotation,
                    flipHorizontal = state.flipHorizontal,
                    flipVertical = state.flipVertical,
                    cropCenter = false
                )

                val uriString = savePreviewImageUseCase(
                    image = transformed,
                    fileName = "ScanLink_Filtered_${System.currentTimeMillis()}"
                )
                uriString
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

    fun applyCrop() {
        val state = _uiState.value
        val imageToCrop = originalImage ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }

            runCatching {
                val cropped = cropPreviewImageUseCase(
                    image = imageToCrop,
                    cropRect = state.cropRect
                )
                originalImage = cropped

                val uriString = savePreviewImageUseCase(
                    image = cropped,
                    fileName = "ScanLink_crop_${System.currentTimeMillis()}"
                )
                uriString
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
