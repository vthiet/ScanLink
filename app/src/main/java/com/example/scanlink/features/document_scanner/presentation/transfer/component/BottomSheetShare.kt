package com.example.scanlink.features.document_scanner.presentation.transfer.component

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetShare(
    onDismiss: () -> Unit,
    onSystemShare: (Context) -> Unit,
    onPublicLink: () -> Unit,
    onPrivateShare: () -> Unit,
    context: Context
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Share document", style = MaterialTheme.typography.titleLarge)
            ShareSheetItem(
                icon = { Icon(Icons.Default.Link, contentDescription = null) },
                title = "Public Link",
                subtitle = "Create password or expiration protected link",
                onClick = onPublicLink
            )
            ShareSheetItem(
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                title = "Private Share",
                subtitle = "Grant Viewer or Editor access by email",
                onClick = onPrivateShare
            )
            ShareSheetItem(
                icon = { Icon(Icons.Default.IosShare, contentDescription = null) },
                title = "System Share",
                subtitle = "Send a local copy through Android",
                onClick = { onSystemShare(context) }
            )
        }
    }
}

@Composable
private fun ShareSheetItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceContainer
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            ListItem(
                headlineContent = { Text(title) },
                supportingContent = { Text(subtitle) },
                leadingContent = icon
            )
        }
    }
}
