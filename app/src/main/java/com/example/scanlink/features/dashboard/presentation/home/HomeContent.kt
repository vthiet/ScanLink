package com.example.scanlink.features.dashboard.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scanlink.R
import com.example.scanlink.core.ui.components.card.RecentFileItem
import com.example.scanlink.core.ui.components.header.AppHeader
import com.example.scanlink.core.ui.model.FileType
import com.example.scanlink.core.ui.model.RecentFile
import com.example.scanlink.core.ui.model.home.QuickAction
import com.example.scanlink.features.dashboard.presentation.home.components.QuickActionSection
import com.example.scanlink.features.dashboard.presentation.home.components.RecentSection


@Composable
fun HomeContent(
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {

    val quickActions = remember {
        listOf(
            QuickAction(title = "Smart Scan", iconRes = R.drawable.scans_img, iconSize = 33),
            QuickAction(title = "ID Cards", iconRes = R.drawable.id_card, iconSize = 40),
            QuickAction(title = "PDF Tools", iconRes = R.drawable.pdf, iconSize = 25),
            QuickAction(title = "Import IMG", iconRes = R.drawable.import_img, iconSize = 31),
            QuickAction(title = "Translate", iconRes = R.drawable.translate, iconSize = 33),
            QuickAction(title = "ALL", iconRes = R.drawable.all_item, iconSize = 27)
        )
    }

    val documents = remember {
        listOf(
            RecentFile(
                id = "1",
                name = "Tên file 1.docx",
                sizeLabel = "820 KB",
                type = FileType.DOCX,
                createdAt = "2026/04/16"
            ),
            RecentFile(
                id = "2",
                name = "Tên file 1.PDF",
                sizeLabel = "1.8 MB",
                type = FileType.PDF,
                createdAt = "2026/04/16"
            ),
            RecentFile(
                id = "3",
                name = "Tên file 2.PDF",
                sizeLabel = "3.1 MB",
                type = FileType.PDF,
                createdAt = "2026/04/16"
            ),
            RecentFile(
                id = "4",
                name = "Tên file 3.PDF",
                sizeLabel = "2.4 MB",
                type = FileType.PDF,
                createdAt = "2026/04/16"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
    ) {

        AppHeader(showSearchBar = true)

        Spacer(modifier = Modifier.height(12.dp))

        QuickActionSection(actions = quickActions)

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(
                        topStart = 30.dp,
                        topEnd = 30.dp
                    )
                )
        ) {

            RecentSection()

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(documents) { file ->
                    RecentFileItem(file = file)
                }
            }
        }
    }
}