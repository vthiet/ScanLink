package com.example.scanlink.features.dashboard.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreen(
    onFileClick: (String) -> Unit = {}
) {
    HomeContent(onFileClick = onFileClick)
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    HomeScreen()
}
