package com.example.scanlink.features.file_sharing.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
fun HistoryContent() {

    val groupedFiles = remember {
        listOf(
            DateGroup(
                title = "HÔM NAY — 22/05/2026",
                files = listOf(
                    RecentFile(
                        id = "1",
                        name = "Hợp đồng Q2_2026.PDF",
                        type = FileType.PDF,
                        createdAt = "22:14",
                        sizeLabel = "2.4 MB",
                        statusText = "Tải lên",
                        statusColor = Color(0xFF00CFA4)
                    ),
                    RecentFile(
                        id = "2",
                        name = "Biên bản họp tháng 5.PDF",
                        type = FileType.PDF,
                        createdAt = "19:07",
                        sizeLabel = "890 KB",
                        statusText = "Chia sẻ",
                        statusColor = Color(0xFF3498DB)
                    )
                )
            ),
            DateGroup(
                title = "HÔM QUA — 21/05/2026",
                files = listOf(
                    RecentFile(
                        id = "3",
                        name = "CCCD_MatTruoc.JPG",
                        type = FileType.JPG,
                        createdAt = "14:32",
                        sizeLabel = "1.1 MB",
                        statusText = "Quét OCR",
                        statusColor = Color(0xFF9B59B6)
                    ),
                    RecentFile(
                        id = "4",
                        name = "Báo cáo tổng kết Sprint 4.docx",
                        type = FileType.DOCX,
                        createdAt = "16:50",
                        sizeLabel = "540 KB",
                        statusText = "Tải lên",
                        statusColor = Color(0xFF00CFA4)
                    ),
                    RecentFile(
                        id = "5",
                        name = "Tên File 2.PDF",
                        type = FileType.PDF,
                        createdAt = "11:20",
                        sizeLabel = "3.1 MB",
                        statusText = "Chia sẻ",
                        statusColor = Color(0xFF3498DB)
                    )
                )
            )
        )
    }

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
                    RecentFileItem(file = file)
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}