package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
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
import com.example.scanlink.features.document_scanner.presentation.transfer.model.PrivateShareTabState
import com.example.scanlink.features.document_scanner.presentation.transfer.model.SharePermission
import com.example.scanlink.features.document_scanner.presentation.transfer.model.TransferDocumentOption

@Composable
fun PrivateShareContent(
    documents: List<TransferDocumentOption>,
    state: PrivateShareTabState,
    onDocumentSelected: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPermissionChange: (SharePermission) -> Unit,
    onShare: () -> Unit,
    onRemoveAccess: (String) -> Unit,
    onChangePermission: (String, SharePermission) -> Unit
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
                Text("Share With Account", style = MaterialTheme.typography.titleMedium)
                DocumentPickerField(
                    documents = documents,
                    selectedDocumentId = state.selectedDocumentId,
                    onDocumentSelected = onDocumentSelected
                )
                OutlinedTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = { Text("Email") },
                    placeholder = { Text("name@example.com") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                PermissionSegmentedControl(
                    selected = state.permission,
                    onSelected = onPermissionChange
                )
                Button(
                    onClick = onShare,
                    enabled = state.selectedDocumentId != null && state.email.isNotBlank() && !state.isSharing,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (state.isSharing) {
                        CircularProgressIndicator()
                    } else {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                        Text("Share")
                    }
                }
            }
        }

        Text("Shared Users", style = MaterialTheme.typography.titleMedium)
        state.sharedUsers.forEach { user ->
            SharedUserCard(
                item = user,
                onRemoveAccess = { onRemoveAccess(user.id) },
                onChangePermission = { onChangePermission(user.id, it) }
            )
        }
    }
}
