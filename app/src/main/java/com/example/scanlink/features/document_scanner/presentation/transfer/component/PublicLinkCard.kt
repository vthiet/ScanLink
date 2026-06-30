package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.NoEncryption
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.document_scanner.presentation.transfer.model.PublicLinkItem

@Composable
fun PublicLinkCard(
    item: PublicLinkItem,
    onCopyLink: () -> Unit,
    onDisableLink: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Link, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
                    Text(item.documentName, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = item.url,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onCopyLink, enabled = item.isEnabled) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy link")
                }
                IconButton(onClick = onDisableLink, enabled = item.isEnabled) {
                    Icon(Icons.Default.Block, contentDescription = "Disable link")
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("Created ${item.createdDate}") })
                AssistChip(onClick = {}, label = { Text(item.expireDate?.let { "Expires $it" } ?: "No expiry") })
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = {},
                    leadingIcon = {
                        Icon(
                            if (item.passwordEnabled) Icons.Default.Lock else Icons.Default.NoEncryption,
                            contentDescription = null
                        )
                    },
                    label = { Text(if (item.passwordEnabled) "Password enabled" else "No password") }
                )
                AssistChip(onClick = {}, label = { Text(if (item.isEnabled) "Enabled" else "Disabled") })
            }
        }
    }
}
