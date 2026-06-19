package com.example.scanlink.features.file_sharing.presentation.ui.history.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun HistoryDateHeader(
    title: String,
    modifier: Modifier = Modifier
) {

    Text(
        text = title,
        color = Color(0xFF8A8A8A),
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}