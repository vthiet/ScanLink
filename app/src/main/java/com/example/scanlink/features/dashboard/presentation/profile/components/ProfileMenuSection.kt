package com.example.scanlink.features.file_sharing.presentation.ui.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.features.file_sharing.presentation.model.MenuItemData

@Composable
fun ProfileMenuSection(
    title: String,
    items: List<MenuItemData>
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = title,
            color = Color(0xFF444444),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E24))
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    MenuItem(item = item)
                    if (index < items.lastIndex) {
                        Divider(color = Color(0xFF1C1C22), thickness = 1.dp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}