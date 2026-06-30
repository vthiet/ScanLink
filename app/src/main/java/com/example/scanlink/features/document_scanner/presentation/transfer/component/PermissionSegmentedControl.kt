package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.scanlink.features.document_scanner.presentation.transfer.model.SharePermission

@Composable
fun PermissionSegmentedControl(
    selected: SharePermission,
    onSelected: (SharePermission) -> Unit
) {
    val permissions = SharePermission.entries
    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        permissions.forEachIndexed { index, permission ->
            SegmentedButton(
                selected = selected == permission,
                onClick = { onSelected(permission) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = permissions.size)
            ) {
                Text(permission.name)
            }
        }
    }
}
