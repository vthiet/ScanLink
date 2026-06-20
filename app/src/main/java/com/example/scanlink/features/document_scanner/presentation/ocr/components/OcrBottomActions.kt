package com.example.scanlink.features.document_scanner.presentation.ocr.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OcrBottomActions() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Button(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            onClick = {},
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E1E1E)
            )
        ) {

            Icon(Icons.Default.PictureAsPdf, null)

            Spacer(modifier = Modifier.width(6.dp))

            Text("Lưu PDF")
        }

        Button(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            onClick = {},
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2ABA8A)
            )
        ) {

            Icon(Icons.Default.CloudUpload, null)

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                "Cloud",
                color = Color.Black
            )
        }
    }
}