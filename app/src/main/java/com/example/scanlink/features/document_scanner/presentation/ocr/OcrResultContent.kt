package com.example.scanlink.features.document_scanner.presentation.ocr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.scanlink.features.document_scanner.presentation.ocr.components.*

@Composable
fun OcrResultContent(
    detectedText: String,
    pdfPath: String?,
    onBackClick: () -> Unit,
    onSaveToDbClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
    ) {
        OcrTopBar(
            onBackClick = onBackClick
        )

        DocumentInfoCard(
            pdfPath = pdfPath
        )

        OcrActionRow(
            textToCopy = detectedText,
            pdfPath = pdfPath
        )

        OcrTextCard(
            text = detectedText,
            modifier = Modifier.weight(1f)
        )

        OcrBottomActions(
            onCancelClick = onBackClick,
            onSaveClick = onSaveToDbClick,
            isSaveEnabled = pdfPath != null
        )
    }
}