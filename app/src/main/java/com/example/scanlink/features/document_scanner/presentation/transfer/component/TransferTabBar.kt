package com.example.scanlink.features.document_scanner.presentation.transfer.component

import androidx.compose.material3.TabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.scanlink.features.document_scanner.presentation.transfer.model.TransferTab

@Composable
fun TransferTabBar(
    selectedTab: TransferTab,
    onTabSelected: (TransferTab) -> Unit
) {
    val tabs = TransferTab.entries
    TabRow(selectedTabIndex = tabs.indexOf(selectedTab)) {
        tabs.forEach { tab ->
            Tab(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                text = { Text(tab.label) }
            )
        }
    }
}
