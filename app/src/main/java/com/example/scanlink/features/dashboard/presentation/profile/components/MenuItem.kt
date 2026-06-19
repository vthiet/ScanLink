package com.example.scanlink.features.file_sharing.presentation.ui.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.features.file_sharing.presentation.model.MenuItemData

@Composable
fun MenuItem(item: MenuItemData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(item.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.color,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Text Content
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = if (item.title.contains("Đăng xuất")) Color(0xFFFF7777) else Color(0xFFCCCCCC),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = item.subtitle,
                color = Color(0xFF444444),
                fontSize = 12.sp
            )
        }

        // Badge or Toggle
        if (item.badge != null) {
            Text(
                text = item.badge,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .background(
                        color = if (item.badge == "2FA") Color(0xFFE8722A).copy(alpha = 0.2f)
                        else Color(0xFF00CFA4).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                color = if (item.badge == "2FA") Color(0xFFE8722A) else Color(0xFF00CFA4),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        } else if (item.isToggle) {
            ToggleSwitch(isChecked = item.toggleState)
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF333333),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}