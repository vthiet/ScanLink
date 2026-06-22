package com.example.scanlink.features.document_scanner.presentation.ocr.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OcrActionRow(
    textToCopy: String,
    pdfPath: String?
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        ActionButton(
            modifier = Modifier.weight(1f),
            text = "Sao chép",
            icon = Icons.Default.ContentCopy,
            isPrimary = true
        )

        ActionButton(
            modifier = Modifier.weight(1f),
            text = "Dịch",
            icon = Icons.Default.Language,
//            onClick = { /* Logic dịch văn bản nếu có */ }
        )

        ActionButton(
            modifier = Modifier.weight(1f),
            text = "Chia sẻ",
            icon = Icons.Default.Share,
//            onClick = {/* Logic chia sẻ text / file PDF */}
        )
    }
}