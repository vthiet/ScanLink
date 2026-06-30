package com.example.scanlink.features.document_scanner.presentation.batch_preview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun BatchPageItem(
    imageUri: String,
    pageNumber: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    val borderColor = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outlineVariant
    }

    Box(
        modifier = Modifier
            .size(width = 72.dp, height = 88.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(if (selected) 2.dp else 1.dp, borderColor, shape)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Page $pageNumber",
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(MaterialTheme.colorScheme.primary)
                .size(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = pageNumber.toString(),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
