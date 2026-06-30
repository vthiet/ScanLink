package com.example.scanlink.features.file_sharing.presentation.history

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HistoryScreen(
    onFileClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    HistoryContent(
        onFileClick = onFileClick,
        onProfileClick = onProfileClick
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HistoryScreenPreview() {
    HistoryContent()
}
