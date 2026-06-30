package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Visibility
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
import com.example.scanlink.features.document_scanner.presentation.transfer.model.SharePermission
import com.example.scanlink.features.document_scanner.presentation.transfer.model.SharedUserItem

@Composable
fun SharedUserCard(
    item: SharedUserItem,
    onRemoveAccess: () -> Unit,
    onChangePermission: (SharePermission) -> Unit,
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
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.email.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(item.email, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.SemiBold)
                Text(item.documentName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                AssistChip(
                    onClick = {
                        onChangePermission(
                            if (item.permission == SharePermission.Viewer) SharePermission.Editor else SharePermission.Viewer
                        )
                    },
                    leadingIcon = {
                        Icon(
                            if (item.permission == SharePermission.Viewer) Icons.Default.Visibility else Icons.Default.Edit,
                            contentDescription = null
                        )
                    },
                    label = { Text(item.permission.name) }
                )
            }
            IconButton(
                onClick = {
                    onChangePermission(
                        if (item.permission == SharePermission.Viewer) SharePermission.Editor else SharePermission.Viewer
                    )
                }
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Change permission")
            }
            IconButton(onClick = onRemoveAccess) {
                Icon(Icons.Default.Delete, contentDescription = "Remove access", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
