package com.example.scanlink.features.document_scanner.presentation.transfer

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, showSystemUi = true)

@Composable
fun TransferScreen(
    onProfileClick: () -> Unit = {}
) {
    TransferContent(onProfileClick = onProfileClick)
}
