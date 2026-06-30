package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.document_scanner.presentation.transfer.model.UploadTabState

@Composable
fun UploadContent(
    state: UploadTabState,
    onSelectFileClick: () -> Unit,
    onRetry: (String) -> Unit,
    onCancel: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onSelectFileClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.CloudUpload, contentDescription = null)
            Text("Upload document")
        }

        Text(
            text = "Upload Status",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (state.items.isEmpty()) {
            OutlinedButton(onClick = onSelectFileClick, modifier = Modifier.fillMaxWidth()) {
                Text("Select a document to sync")
            }
        } else {
            state.items.forEach { item ->
                UploadCard(
                    item = item,
                    onRetry = { onRetry(item.id) },
                    onCancel = { onCancel(item.id) }
                )
            }
        }
    }
}
