package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.features.file_sharing.presentation.model.transfer.TransferTab

private val ColorBg = Color(0xFF111113)
private val ColorAccent = Color(0xFF00CFA4)
private val ColorAccentText = Color(0xFF0A2E26)
private val ColorSurface = Color(0xFF1C1C21)
private val ColorBorder = Color(0xFF252528)
private val ColorTextMuted = Color(0xFF666666)

private fun tabIconText(tab: TransferTab): String = when (tab) {
    TransferTab.Upload  -> "󰅧"
    TransferTab.Share   -> "󰒓"
    TransferTab.Manage  -> "󰌹"
}

// Maps each tab to a display label with icon emoji fallback
private fun tabDisplayLabel(tab: TransferTab): String = when (tab) {
    TransferTab.Upload  -> "↑  Tải lên"
    TransferTab.Share   -> "⤴  Chia sẻ"
    TransferTab.Manage  -> "🔗  Quản lý"
}

@Composable
fun TransferActionSection(
    selectedTab: TransferTab,
    onTabSelected: (TransferTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ColorBg)
            .padding(horizontal = 16.dp, vertical = 0.dp)
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TransferTab.entries.forEach { tab ->
            val isActive = tab == selectedTab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isActive) ColorAccent else ColorSurface)
                    .border(
                        width = 1.dp,
                        color = if (isActive) ColorAccent else ColorBorder,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab.label,
                    color = if (isActive) ColorAccentText else ColorTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}