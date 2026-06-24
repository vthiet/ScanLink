package com.example.scanlink.features.document_scanner.presentation.ocr.components

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

@Composable
fun OcrActionRow(
    textToCopy: String,
    pdfPath: String?
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

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
            isPrimary = true,
            onClick = {
                if (textToCopy.isNotEmpty()) {
                    clipboardManager.setText(AnnotatedString(textToCopy))
                    Toast.makeText(context, "Đã sao chép vào bộ nhớ tạm", Toast.LENGTH_SHORT).show()
                }
            }
        )

        ActionButton(
            modifier = Modifier.weight(1f),
            text = "Dịch đoạn",
            icon = Icons.Default.Language,
            onClick = { 
                // Logic dịch văn bản có thể tích hợp sau
                Toast.makeText(context, "Tính năng dịch đang được phát triển", Toast.LENGTH_SHORT).show()
            }
        )

        ActionButton(
            modifier = Modifier.weight(1f),
            text = "Chia sẻ",
            icon = Icons.Default.Share,
            onClick = {
                // Logic chia sẻ có thể tích hợp sau
                Toast.makeText(context, "Tính năng chia sẻ đang được phát triển", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
