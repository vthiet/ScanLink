package com.example.scanlink.features.dashboard.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scanlink.core.ui.model.MenuItemData
import com.example.scanlink.features.dashboard.presentation.profile.components.LogoutSection
import com.example.scanlink.features.dashboard.presentation.profile.components.ProfileHeroSection
import com.example.scanlink.features.dashboard.presentation.profile.components.ProfileMenuSection
import com.example.scanlink.features.dashboard.presentation.profile.components.StorageCard

@Composable
fun ProfileContent() {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111113))
            .verticalScroll(scrollState)
    ) {
        // Hero Section
        ProfileHeroSection()

        Spacer(modifier = Modifier.height(16.dp))

        // Storage Card
        StorageCard()

        Spacer(modifier = Modifier.height(16.dp))

        // Các sections
        ProfileMenuSection(
            title = "Tài khoản",
            items = listOf(
                MenuItemData(
                    icon = Icons.Default.Person,
                    title = "Thông tin cá nhân",
                    subtitle = "Tên, email, số điện thoại",
                    color = Color(0xFF00CFA4)
                ),
                MenuItemData(
                    icon = Icons.Default.Link,
                    title = "Liên kết tài khoản",
                    subtitle = "Google đã kết nối",
                    color = Color(0xFF6644FF),
                    badge = "Đã liên kết"
                )
            )
        )

        ProfileMenuSection(
            title = "Tuỳ chỉnh",
            items = listOf(
                MenuItemData(
                    icon = Icons.Default.DarkMode,
                    title = "Giao diện tối",
                    subtitle = "Dark mode đang bật",
                    color = Color.Gray,
                    isToggle = true,
                    toggleState = true
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
            )
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
                    subtitle = "Quyền riêng tư, GDPR",
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

        // Logout
        LogoutSection()

        Spacer(modifier = Modifier.height(80.dp))
    }
}