package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.scanlink.features.document_scanner.presentation.transfer.model.TransferDocumentOption

@Composable
fun DocumentPickerField(
    documents: List<TransferDocumentOption>,
    selectedDocumentId: String?,
    onDocumentSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = documents.firstOrNull { it.id == selectedDocumentId }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected?.title ?: "No document selected",
            onValueChange = {},
            readOnly = true,
            label = { Text("Document") },
            supportingText = selected?.let { { Text(it.sizeLabel) } },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select document")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            documents.forEach { document ->
                DropdownMenuItem(
                    text = { Text(document.title) },
                    onClick = {
                        onDocumentSelected(document.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
