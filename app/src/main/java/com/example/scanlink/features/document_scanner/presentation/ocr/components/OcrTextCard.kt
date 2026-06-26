package com.example.scanlink.features.document_scanner.presentation.ocr.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OcrTextCard(
    text: String,
    modifier: Modifier = Modifier
) {
    // Sử dụng state local để cho phép chỉnh sửa trực tiếp trên màn hình kết quả
    var editableText by remember(text) { mutableStateOf(text) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B1B1D)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            TextField(
                value = editableText,
                onValueChange = { editableText = it },
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                textStyle = TextStyle(
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    fontFamily = FontFamily.Monospace
                ),
                placeholder = {
                    Text("Không tìm thấy nội dung chữ.", color = Color.Gray)
                }
            )
        }
    }
}
