package com.example.scanlink.features.document_scanner.presentation.ocr.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.scanlink.features.document_scanner.presentation.camera.ScanFilterType

@Composable
fun FilterSelector(
    selectedFilter: ScanFilterType,
    onFilterSelected: (ScanFilterType) -> Unit
) {
    val filters = listOf(
        FilterItem("Gốc", ScanFilterType.ORIGINAL),
        FilterItem("B&W", ScanFilterType.B_W),
        FilterItem("Xám", ScanFilterType.GRAYSCALE),
        FilterItem("Magic", ScanFilterType.MAGIC_COLOR)
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filters) { item ->
            val isSelected = selectedFilter == item.type
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isSelected) Color(0xFF2ABA8A) else Color(0xFF2A2A2A))
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color(0xFF00CFA4) else Color.Transparent,
                        shape = RoundedCornerShape(18.dp)
                    )
                    .clickable { onFilterSelected(item.type) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.name,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

data class FilterItem(val name: String, val type: ScanFilterType)
