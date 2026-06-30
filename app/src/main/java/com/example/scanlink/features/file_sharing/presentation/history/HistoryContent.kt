package com.example.scanlink.features.file_sharing.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanlink.core.ui.model.FileType
import com.example.scanlink.core.ui.model.RecentFile
import com.example.scanlink.core.ui.components.card.RecentFileItem
import com.example.scanlink.core.ui.components.header.AppHeader
import com.example.scanlink.features.file_sharing.presentation.history.components.HistoryDateHeader
import com.example.scanlink.features.file_sharing.presentation.history.components.HistorySearchBar
import com.example.scanlink.features.file_sharing.presentation.history.components.HistoryTabBar

data class DateGroup(
    val title: String,
    val files: List<RecentFile>
)

@Composable
fun HistoryContent(
    onFileClick: (String) -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val groupedFiles = uiState.groupedFiles

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AppHeader(showSearchBar = false)

        HistorySearchBar()

        Spacer(modifier = Modifier.height(24.dp))


        HistoryTabBar()

        Spacer(modifier = Modifier.height(24.dp))


        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            groupedFiles.forEach { group ->
                // Date Header
                item {
                    HistoryDateHeader(
                        title = group.title,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                // Files trong group đó
                items(group.files) { file ->
                    RecentFileItem(
                        file = file,
                        onClick = { onFileClick(file.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
