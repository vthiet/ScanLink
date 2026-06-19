package com.example.scanlink.features.file_sharing.presentation.ui.component.header

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppHeader(
    modifier: Modifier = Modifier,
    showSearchBar: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // Logo
        Row {
            Text(
                text = "Scan",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Link",
                color = Color(0xFF00E0A4),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        if (showSearchBar) {
            var searchText by remember { mutableStateOf("") }

            BasicTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                singleLine = true,
                textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .clip(RoundedCornerShape(40.dp))
                            .background(Color(0xFF2A2A2F))
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchText.isEmpty()) {
                                Text(
                                    text = "Tìm kiếm...",
                                    color = Color.LightGray,
                                    fontSize = 14.sp
                                )
                            }
                            innerTextField()
                        }

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color(0xFF00E0A4)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(16.dp))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // Avatar
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF00E0A4)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Q",
                color = Color(0xFF0A2E26),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}