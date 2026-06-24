package com.example.scanlink.features.document_scanner.presentation.ocr

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.document_scanner.presentation.ocr.components.*

@Composable
fun OcrResultContent(
    processedBitmap: Bitmap?,
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

        // Hiển thị ảnh đã scan ở trên
        processedBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Scanned Document",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black),
                contentScale = ContentScale.Fit
            )
        }

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
