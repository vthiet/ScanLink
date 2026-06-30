package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.scanlink.features.document_scanner.presentation.transfer.model.TransferDocumentOption

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentPickerField(
    documents: List<TransferDocumentOption>,
    selectedDocumentId: String?,
    onDocumentSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selected = documents.firstOrNull { it.id == selectedDocumentId }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected?.title ?: "No document selected",
            onValueChange = {},
            readOnly = true,
            label = { Text("Document") },
            supportingText = selected?.let { { Text(it.sizeLabel) } },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
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
