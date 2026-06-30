package com.example.scanlink.features.document_scanner.presentation.batch_preview

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BatchBottomBar(
    canActOnPage: Boolean,
    cropMode: Boolean,
    isExporting: Boolean,
    onDeletePage: () -> Unit,
    onRetakePage: () -> Unit,
    onContinueScanning: () -> Unit,
    onRotatePage: () -> Unit,
    onCropPage: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onRotatePage,
                enabled = canActOnPage && !isExporting && !cropMode,
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.RotateRight, contentDescription = null)
                Text(text = "Rotate")
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            OutlinedButton(
                onClick = onCropPage,
                enabled = canActOnPage && !isExporting && !cropMode,
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Crop, contentDescription = null)
                Text(text = "Crop")
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            OutlinedButton(
                onClick = onDeletePage,
                enabled = canActOnPage && !isExporting,
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Text(text = "Delete")
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            OutlinedButton(
                onClick = onRetakePage,
                enabled = canActOnPage && !isExporting,
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                Text(text = "Retake")
            }

            Spacer(modifier = Modifier.padding(horizontal = 4.dp))

            OutlinedButton(
                onClick = onContinueScanning,
                enabled = !isExporting && !cropMode,
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null)
                Text(text = "Scan")
            }
        }
    }
}
