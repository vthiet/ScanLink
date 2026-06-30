package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.document_scanner.presentation.transfer.model.UploadDocumentItem
import com.example.scanlink.features.document_scanner.presentation.transfer.model.UploadState

@Composable
fun UploadCard(
    item: UploadDocumentItem,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(item.sizeLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LinearProgressIndicator(
                    progress = { item.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                AssistChip(
                    onClick = {},
                    label = { Text(item.state.label()) },
                    leadingIcon = {
                        Icon(
                            imageVector = when (item.state) {
                                UploadState.Uploading -> Icons.Default.Sync
                                UploadState.Synced -> Icons.Default.CheckCircle
                                UploadState.Failed -> Icons.Default.Error
                            },
                            contentDescription = null
                        )
                    }
                )
            }
            when (item.state) {
                UploadState.Uploading -> IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Cancel, contentDescription = "Cancel upload")
                }

                UploadState.Failed -> IconButton(onClick = onRetry) {
                    Icon(Icons.Default.Refresh, contentDescription = "Retry upload")
                }

                UploadState.Synced -> Unit
            }
        }
    }
}

private fun UploadState.label(): String {
    return when (this) {
        UploadState.Uploading -> "Uploading"
        UploadState.Synced -> "Synced"
        UploadState.Failed -> "Failed"
    }
}
