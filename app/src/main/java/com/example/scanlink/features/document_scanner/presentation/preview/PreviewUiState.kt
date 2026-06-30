package com.example.scanlink.features.document_scanner.presentation.preview

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.entities.CropRect
import com.example.scanlink.features.document_scanner.domain.entities.ScanFilterType

data class PreviewUiState(
    val imageUri: String = "",
    val rotation: Float = 0f,
    val flipHorizontal: Boolean = false,
    val flipVertical: Boolean = false,
    val cropMode: Boolean = false,
    val cropRect: CropRect = CropRect(),
    val isSaving: Boolean = false,
    val savedUri: String? = null,
    val errorMessage: String? = null,
    val selectedFilter: ScanFilterType = ScanFilterType.ORIGINAL,
    val previewBitmap: Bitmap? = null
)
