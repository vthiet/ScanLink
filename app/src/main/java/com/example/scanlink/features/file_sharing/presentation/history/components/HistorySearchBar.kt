package com.example.scanlink.features.file_sharing.presentation.ui.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HistorySearchBar() {

    var search by remember {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = Color(0xFF17181C),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(horizontal = 14.dp, vertical = 12.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color.Gray
        )

        Spacer(modifier = Modifier.width(10.dp))

        BasicTextField(
            value = search,
            onValueChange = {
                search = it
            },
            singleLine = true,
            textStyle = TextStyle(
                color = Color.White,
                fontSize = 14.sp
            ),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->

                Box {

                    if (search.isEmpty()) {
                        Text(
                            text = "Tìm kiếm trong lịch sử...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }

                    innerTextField()
                }
            }
        )

        Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = null,
            tint = Color(0xFF00E0A4)
        )
    }
}