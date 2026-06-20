package com.example.scanlink.features.document_scanner.presentation.ocr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.scanlink.features.document_scanner.presentation.ocr.components.*

@Composable
fun OcrResultContent() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
    ) {

        OcrTopBar()

        DocumentInfoCard()

        OcrActionRow()

        OcrTextCard(
            modifier = Modifier.weight(1f)
        )

        OcrBottomActions()
    }
}