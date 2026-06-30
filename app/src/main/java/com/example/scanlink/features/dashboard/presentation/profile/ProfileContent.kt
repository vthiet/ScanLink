package com.example.scanlink.features.dashboard.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scanlink.core.ui.model.MenuItemData
import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.dashboard.presentation.preferences.DashboardPreferencesEvent
import com.example.scanlink.features.dashboard.presentation.preferences.DashboardPreferencesState
import com.example.scanlink.features.dashboard.presentation.profile.components.LogoutSection
import com.example.scanlink.features.dashboard.presentation.profile.components.ProfileHeroSection
import com.example.scanlink.features.dashboard.presentation.profile.components.ProfileMenuSection
import com.example.scanlink.features.dashboard.presentation.profile.components.StorageCard

@Composable
fun ProfileContent(
    profileState: ProfileState,
    preferencesState: DashboardPreferencesState,
    onProfileEvent: (ProfileEvent) -> Unit,
    onPreferencesEvent: (DashboardPreferencesEvent) -> Unit
) {
    val scrollState = rememberScrollState()
    val user = profileState.user
    val displayName = user?.displayName?.takeIf { it.isNotBlank() }
        ?: user?.email?.substringBefore("@")
        ?: "Người dùng ScanLink"
    val email = user?.email?.takeIf { it.isNotBlank() } ?: "Chưa có email"
    val providerLabel = user.providerLabel()

    if (profileState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        profileState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        ProfileHeroSection(
            displayName = displayName,
            email = email,
            photoUrl = user?.photoUrl,
            providerLabel = providerLabel
        )

        Spacer(modifier = Modifier.height(16.dp))

        StorageCard()

        Spacer(modifier = Modifier.height(16.dp))

        ProfileMenuSection(
            title = "Tài khoản",
            items = listOf(
                MenuItemData(
                    icon = Icons.Default.Person,
                    title = "Thông tin tài khoản",
                    subtitle = listOfNotNull(email, user?.role?.takeIf { it.isNotBlank() })
                        .joinToString(" · ")
                        .ifBlank { "Xem thông tin đăng nhập" },
                    color = Color(0xFF00CFA4)
                ),
                MenuItemData(
                    icon = Icons.Default.Link,
                    title = "Liên kết tài khoản",
                    subtitle = providerLabel,
                    color = Color(0xFF6644FF),
                    badge = if (user?.providerId.isNullOrBlank()) null else "Đã liên kết"
                )
            ),
            onItemClick = { item ->
                if (item.title == "Thông tin tài khoản") {
                    onProfileEvent(ProfileEvent.AccountDetailsClicked)
                }
            }
        )

        ProfileMenuSection(
            title = "Tuỳ chỉnh",
            items = listOf(
                MenuItemData(
                    icon = Icons.Default.DarkMode,
                    title = if (preferencesState.isDarkTheme) "Giao diện tối" else "Giao diện sáng",
                    subtitle = if (preferencesState.isDarkTheme) {
                        "Đang sử dụng Dark Mode"
                    } else {
                        "Đang sử dụng Light Mode"
                    },
                    color = MaterialTheme.colorScheme.primary,
                    isToggle = true,
                    toggleState = preferencesState.isDarkTheme
                ),
                MenuItemData(
                    icon = Icons.Default.Language,
                    title = "Ngôn ngữ",
                    subtitle = "Tiếng Việt",
                    color = Color(0xFF00CFA4)
                ),
                MenuItemData(
                    icon = Icons.Default.Settings,
                    title = "Chất lượng quét mặc định",
                    subtitle = "Cao · 300 DPI",
                    color = Color(0xFFE8722A)
                )
            ),
            onToggleClick = { currentValue ->
                onPreferencesEvent(DashboardPreferencesEvent.DarkThemeChanged(!currentValue))
            }
        )

        ProfileMenuSection(
            title = "Hỗ trợ",
            items = listOf(
                MenuItemData(
                    icon = Icons.Default.Help,
                    title = "Trung tâm trợ giúp",
                    subtitle = "Hướng dẫn & FAQ",
                    color = Color(0xFF3A6FFF)
                ),
                MenuItemData(
                    icon = Icons.Default.Description,
                    title = "Điều khoản & Chính sách",
                    subtitle = "Quyền riêng tư, bảo mật dữ liệu",
                    color = Color.Gray
                ),
                MenuItemData(
                    icon = Icons.Default.Star,
                    title = "Đánh giá ứng dụng",
                    subtitle = "Ủng hộ đội phát triển",
                    color = Color(0xFF00CFA4)
                )
            )
        )

        LogoutSection(
            isLoggingOut = profileState.isLoggingOut,
            onLogoutClick = { onProfileEvent(ProfileEvent.LogoutClicked) }
        )

        Spacer(modifier = Modifier.height(80.dp))
    }

    if (profileState.showAccountDetails) {
        AccountDetailsDialog(
            user = user,
            displayName = displayName,
            email = email,
            providerLabel = providerLabel,
            onRenameClick = { onProfileEvent(ProfileEvent.RenameClicked) },
            onDismiss = { onProfileEvent(ProfileEvent.AccountDetailsDismissed) }
        )
    }

    if (profileState.showRenameDialog) {
        RenameDialog(
            value = profileState.displayNameInput,
            error = profileState.displayNameError,
            isUpdating = profileState.isUpdatingName,
            onValueChange = { onProfileEvent(ProfileEvent.DisplayNameChanged(it)) },
            onConfirm = { onProfileEvent(ProfileEvent.RenameConfirmed) },
            onDismiss = { onProfileEvent(ProfileEvent.RenameDismissed) }
        )
    }

    if (profileState.showLogoutConfirmation) {
        AlertDialog(
            onDismissRequest = { onProfileEvent(ProfileEvent.LogoutDismissed) },
            title = { Text("Đăng xuất") },
            text = { Text("Bạn có chắc muốn đăng xuất khỏi tài khoản hiện tại?") },
            confirmButton = {
                Button(onClick = { onProfileEvent(ProfileEvent.LogoutConfirmed) }) {
                    Text("Đăng xuất")
                }
            },
            dismissButton = {
                TextButton(onClick = { onProfileEvent(ProfileEvent.LogoutDismissed) }) {
                    Text("Huỷ")
                }
            }
        )
    }
}

@Composable
private fun AccountDetailsDialog(
    user: UserEntity?,
    displayName: String,
    email: String,
    providerLabel: String,
    onRenameClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thông tin tài khoản") },
        text = {
            Column {
                AccountDetailRow("Tên hiển thị", displayName)
                AccountDetailRow("Email", email)
                AccountDetailRow("Nhà cung cấp", providerLabel)
                AccountDetailRow("Vai trò", user?.role ?: "USER")
                AccountDetailRow(
                    label = "Trạng thái",
                    value = if (user?.isActive == false) "Đã khoá" else "Đang hoạt động"
                )
            }
        },
        confirmButton = {
            Button(onClick = onRenameClick) {
                Text("Đổi tên")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}

@Composable
private fun RenameDialog(
    value: String,
    error: String?,
    isUpdating: Boolean,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            if (!isUpdating) onDismiss()
        },
        title = { Text("Đổi tên người dùng") },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text("Tên hiển thị") },
                    singleLine = true,
                    isError = error != null
                )
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isUpdating
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                } else {
                    Text("Lưu")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isUpdating
            ) {
                Text("Huỷ")
            }
        }
    )
}

@Composable
private fun AccountDetailRow(
    label: String,
    value: String
) {
    Text(
        text = "$label: $value",
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(vertical = 3.dp)
    )
}

private fun UserEntity?.providerLabel(): String {
    return when (this?.providerId) {
        "google.com" -> "Google"
        "password" -> "Email/Password"
        null, "" -> "Firebase"
        else -> this.providerId
    }
}
