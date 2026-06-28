package com.example.scanlink.features.document_scanner.presentation.camera

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.entities.ScanFilterType

data class CameraUiStateHolder(
    val selectedMode: String = "Quét",
    val flashEnabled: Boolean = false,
    val isFrontCamera: Boolean = false,
    val uiState: CameraUiState = CameraUiState.Initial,
    val isLoading: Boolean = false,
    val originalBitmap: Bitmap? = null,
    val transformedBitmap: Bitmap? = null,
    val selectedFilter: ScanFilterType = ScanFilterType.ORIGINAL,

    val processedBitmap: Bitmap? = null,
    val detectedText: String = "",
    val pdfPath: String? = null,
    val capturedImageUri: String? = null,
    val capturedImages: List<String> = emptyList()

)
