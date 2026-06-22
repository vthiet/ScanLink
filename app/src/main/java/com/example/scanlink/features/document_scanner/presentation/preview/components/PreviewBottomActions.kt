package com.example.scanlink.features.document_scanner.presentation.preview.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PreviewBottomActions(
    onRetake: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        OutlinedButton(
            modifier = Modifier.weight(1f),
            onClick = onRetake
        ) {
            Text("Chụp lại")
        }

        Button(
            modifier = Modifier.weight(1f),
            onClick = onSave
        ) {
            Text("Tiếp tục")
        }
    }
}