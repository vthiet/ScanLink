package com.example.scanlink.features.document_scanner.presentation.ocr

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanlink.features.document_scanner.presentation.camera.CameraViewModel

@Composable
fun OcrResultScreen(
    onBackClick: () -> Unit,
    viewModel: CameraViewModel
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    OcrResultContent(
        processedBitmap = state.processedBitmap,
        detectedText = state.detectedText,
        pdfPath = state.pdfPath,
        onBackClick = onBackClick,
        onSaveToDbClick = {
            viewModel.saveDocument()
        }
    )
}
