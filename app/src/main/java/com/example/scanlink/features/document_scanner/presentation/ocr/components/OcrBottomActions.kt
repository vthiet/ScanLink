package com.example.scanlink.features.document_scanner.presentation.ocr.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OcrBottomActions(
    onCancelClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    isSaveEnabled: Boolean = false
) {

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
            onClick = onCancelClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E1E1E)
            )
        ) {
            Icon(Icons.Default.Cancel, contentDescription = "Hủy bỏ")
            Spacer(modifier = Modifier.width(6.dp))
            Text("Hủy bỏ", color = Color.White)
        }

        Button(
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            onClick = onSaveClick,
            enabled = isSaveEnabled,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2ABA8A),
                disabledContainerColor = Color(0xFF1E1E1E).copy(alpha = 0.5f)
            )
        ) {
            Icon(
                Icons.Default.CloudUpload,
                contentDescription = "Lưu",
                tint = if (isSaveEnabled) Color.Black else Color.Gray
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "Lưu lại",
                color = if (isSaveEnabled) Color.Black else Color.Gray
            )
        }
    }
}