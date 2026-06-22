package com.example.scanlink.features.document_scanner.presentation.ocr.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun OcrTopBar(
    onBackClick: () -> Unit = {}
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = {}) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1E1E1E), CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    Icons.Default.ArrowBack,
                    null,
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                "Kết quả OCR",
                color = Color.White,
                fontSize = 20.sp
            )

            Text(
                "Đã nhận dạng văn bản",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }

        IconButton(onClick = {}) {
            Icon(
                Icons.Default.MoreVert,
                null,
                tint = Color.Gray
            )
        }
    }
}