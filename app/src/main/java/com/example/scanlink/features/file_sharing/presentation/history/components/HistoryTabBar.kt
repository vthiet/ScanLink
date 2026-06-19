package com.example.scanlink.features.file_sharing.presentation.ui.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HistoryTabBar() {

    val tabs = listOf(
        "Tất cả",
        "Đã quét",
        "Đã chia sẻ",
        "Đã tải",
        "Đã xóa",
        "Yêu thích",
        "Gần đây"
    )

    var selectedTab by remember {
        mutableStateOf("Tất cả")
    }

    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        items(tabs) { tab ->

            val isSelected = selectedTab == tab

            Text(
                text = tab,
                color = if (isSelected) Color.Black else Color(0xFFB8B8B8),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .background(
                        color = if (isSelected)
                            Color(0xFF00E0A4)
                        else
                            Color(0xFF1A1A1D),
                        shape = RoundedCornerShape(30.dp)
                    )
                    .clickable {
                        selectedTab = tab
                    }
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    )
            )
        }
    }
}