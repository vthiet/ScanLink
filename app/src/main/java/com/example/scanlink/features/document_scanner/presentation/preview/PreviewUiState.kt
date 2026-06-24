package com.example.scanlink.features.document_scanner.presentation.preview

data class CropRect(
    val left: Float = 0.08f,
    val top: Float = 0.08f,
    val right: Float = 0.92f,
    val bottom: Float = 0.92f
)

data class PreviewUiState(
    val imageUri: String = "",
    val rotation: Float = 0f,
    val flipHorizontal: Boolean = false,
    val flipVertical: Boolean = false,
    val cropMode: Boolean = false,
    val cropRect: CropRect = CropRect(),
    val isSaving: Boolean = false,
    val savedUri: String? = null,
    val errorMessage: String? = null
)