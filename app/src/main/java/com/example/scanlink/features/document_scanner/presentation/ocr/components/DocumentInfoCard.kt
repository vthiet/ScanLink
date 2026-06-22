package com.example.scanlink.features.document_scanner.presentation.ocr.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DocumentInfoCard() {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B1B1D)
        ),
        shape = RoundedCornerShape(18.dp)
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                "TÊN TÀI LIỆU",
                color = Color.Gray,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Purchase Agreement",
                color = Color.White,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "24 Nov 2025 • English • 1 trang",
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}