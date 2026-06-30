package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.document_scanner.presentation.transfer.model.PublicShareTabState
import com.example.scanlink.features.document_scanner.presentation.transfer.model.TransferDocumentOption

@Composable
fun PublicShareContent(
    documents: List<TransferDocumentOption>,
    state: PublicShareTabState,
    onDocumentSelected: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onExpireDaysChange: (String) -> Unit,
    onGenerateLink: () -> Unit,
    onCopyLink: (String) -> Unit,
    onDisableLink: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Create Public Link", style = MaterialTheme.typography.titleMedium)
                DocumentPickerField(
                    documents = documents,
                    selectedDocumentId = state.selectedDocumentId,
                    onDocumentSelected = onDocumentSelected
                )
                OutlinedTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = { Text("Password") },
                    placeholder = { Text("Optional") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.expireDays,
                    onValueChange = onExpireDaysChange,
                    label = { Text("Expiration Date") },
                    placeholder = { Text("Optional days") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = onGenerateLink,
                    enabled = state.selectedDocumentId != null && !state.isGenerating,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isGenerating) {
                        CircularProgressIndicator()
                    } else {
                        Icon(Icons.Default.Link, contentDescription = null)
                        Text("Generate Link")
                    }
                }
            }
        }

        Text("Public Links", style = MaterialTheme.typography.titleMedium)
        state.links.forEach { link ->
            PublicLinkCard(
                item = link,
                onCopyLink = { onCopyLink(link.id) },
                onDisableLink = { onDisableLink(link.id) }
            )
        }
    }
}
