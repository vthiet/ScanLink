package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun UploadAreaCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF1A1A1E))
            .border(1.dp, Color(0xFF2A2A2F), RoundedCornerShape(20.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CloudUpload,
            contentDescription = null,
            tint = Color(0xFF00CFA4),
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Kéo thả hoặc chọn file",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )

        Text(
            text = "Hỗ trợ PDF, DOCX, JPG - Tối đa 50MB",
            color = Color(0xFF888888),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { /* Mở file picker */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00CFA4)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(48.dp)
        ) {
            Text("Chọn file", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}