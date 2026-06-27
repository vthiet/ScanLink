package com.example.scanlink.features.document_scanner.presentation.file_detail.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RenameDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename file") },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                label = { Text("File name") },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Rename") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete file?") },
        text = { Text("This removes the file from local storage and history.") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ShareOptionsDialog(
    onDismiss: () -> Unit,
    onSystemShare: () -> Unit,
    onPublicLink: () -> Unit,
    onPrivateAccess: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tùy chọn chia sẻ") },
        text = {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextButton(onClick = onSystemShare, modifier = Modifier.fillMaxWidth()) {
                    Text("Chia sẻ qua hệ thống (System Share)", textAlign = androidx.compose.ui.text.style.TextAlign.Start, modifier = Modifier.fillMaxWidth())
                }
                TextButton(onClick = onPublicLink, modifier = Modifier.fillMaxWidth()) {
                    Text("Tạo link chia sẻ công khai (Public Link)", textAlign = androidx.compose.ui.text.style.TextAlign.Start, modifier = Modifier.fillMaxWidth())
                }
                TextButton(onClick = onPrivateAccess, modifier = Modifier.fillMaxWidth()) {
                    Text("Cấp quyền truy cập riêng tư (Private Permission)", textAlign = androidx.compose.ui.text.style.TextAlign.Start, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Hủy") }
        }
    )
}

@Composable
fun PublicLinkDialog(
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    expireDaysValue: String,
    onExpireDaysChange: (String) -> Unit,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tạo liên kết chia sẻ công khai") },
        text = {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = passwordValue,
                    onValueChange = onPasswordChange,
                    singleLine = true,
                    label = { Text("Mật khẩu bảo vệ (Tùy chọn)") },
                    placeholder = { Text("Để trống nếu không cần") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = expireDaysValue,
                    onValueChange = onExpireDaysChange,
                    singleLine = true,
                    label = { Text("Hết hạn trong số ngày (Tùy chọn)") },
                    placeholder = { Text("Ví dụ: 7") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isLoading) { Text("Tạo Link") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Hủy") }
        }
    )
}

@Composable
fun PrivateAccessDialog(
    emailValue: String,
    onEmailChange: (String) -> Unit,
    roleValue: String,
    onRoleChange: (String) -> Unit,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var expanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cấp quyền truy cập riêng tư") },
        text = {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = emailValue,
                    onValueChange = onEmailChange,
                    singleLine = true,
                    label = { Text("Email người nhận") },
                    placeholder = { Text("collaborator@example.com") },
                    modifier = Modifier.fillMaxWidth()
                )

                androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = if (roleValue == "EDITOR") "Chỉnh sửa (EDITOR)" else "Xem (VIEWER)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Vai trò") },
                        trailingIcon = {
                            androidx.compose.material3.IconButton(onClick = { expanded = true }) {
                                androidx.compose.material3.Icon(
                                    imageVector = androidx.compose.material.icons.Icons.Default.ArrowDropDown,
                                    contentDescription = "Chọn vai trò"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                    androidx.compose.material3.DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("Xem (VIEWER)") },
                            onClick = {
                                onRoleChange("VIEWER")
                                expanded = false
                            }
                        )
                        androidx.compose.material3.DropdownMenuItem(
                            text = { Text("Chỉnh sửa (EDITOR)") },
                            onClick = {
                                onRoleChange("EDITOR")
                                expanded = false
                            }
                        )
                    }
                }

                if (isLoading) {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = !isLoading && emailValue.isNotBlank()) { Text("Cấp Quyền") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Hủy") }
        }
    )
}

@Composable
fun PublicLinkSuccessDialog(
    shareLink: String,
    onDismiss: () -> Unit,
    onCopy: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Đã tạo liên kết chia sẻ") },
        text = {
            androidx.compose.foundation.layout.Column(
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Bạn có thể copy liên kết dưới đây để gửi cho mọi người:")
                OutlinedTextField(
                    value = shareLink,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onCopy) { Text("Copy Link") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Đóng") }
        }
    )
}
