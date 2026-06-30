package com.example.scanlink.features.dashboard.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    onFileClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    HomeContent(
        onFileClick = onFileClick,
        onProfileClick = onProfileClick
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
