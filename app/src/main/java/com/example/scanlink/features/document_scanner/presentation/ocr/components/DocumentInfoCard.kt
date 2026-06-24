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
fun DocumentInfoCard(
    pdfPath: String?,
    onClick: () -> Unit = {}
) {
    val fileName = pdfPath?.substringAfterLast("/") ?: "Tai_lieu_scan.pdf"
    val fileDetail = if (pdfPath != null) "Định dạng: PDF • 1 trang" else "Đang chuẩn bị file..."

    Card(
        onClick = onClick,
        enabled = pdfPath != null,
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
                text = fileName,
                color = if (pdfPath != null) Color.White else Color.Gray,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = fileDetail,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}