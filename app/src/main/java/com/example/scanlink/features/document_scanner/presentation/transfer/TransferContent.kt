package com.example.scanlink.features.document_scanner.presentation.transfer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.features.document_scanner.presentation.transfer.component.TransferActionSection
import com.example.scanlink.features.document_scanner.presentation.transfer.component.UploadAreaCard
import com.example.scanlink.features.document_scanner.presentation.transfer.component.UploadingFileItem
import com.example.scanlink.core.ui.model.FileType
import com.example.scanlink.core.ui.model.RecentFile
import com.example.scanlink.features.file_sharing.presentation.model.transfer.TransferTab
import com.example.scanlink.core.ui.components.card.RecentFileItem
import com.example.scanlink.core.ui.components.header.AppHeader

@Composable
fun TransferContent(
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {

    var selectedTab by remember { mutableStateOf(TransferTab.Upload) }

    val uploadingFiles = remember {

        listOf(
            RecentFile(
                id = "1",
                name = "Tên File 1.PDF",
                sizeLabel = "1.8 MB",
                type = FileType.PDF,
                createdAt = "16/04/2028",
                uploadProgress = 0.65f,
                statusText = "Đang tải",
                statusColor = Color(0xFF00CFA4)
            )
        )
    }

    val recentFiles = remember {
        listOf(
            RecentFile(
                id = "2",
                name = "Tên File 1.docx",
                sizeLabel = "820 KB",
                type = FileType.DOCX,
                createdAt = "18/04/2028"
            ),
            RecentFile(
                id = "3",
                name = "Tên File 2.PDF",
                sizeLabel = "3.1 MB",
                type = FileType.PDF,
                createdAt = "13/04/2028"
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F11))
            .padding(paddingValues)
    ) {
        AppHeader(showSearchBar = true)

        TransferActionSection(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Khu vực Upload
            item {
                UploadAreaCard()
            }

            // Đang tải lên
            if (uploadingFiles.isNotEmpty()) {
                item {
                    Text(
                        text = "ĐANG TẢI LÊN",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(uploadingFiles) { file ->
                    UploadingFileItem(file = file)
                }
            }

            // Gần đây
            item {
                Text(
                    text = "GẦN ĐÂY",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            items(recentFiles) { file ->
                RecentFileItem(file = file)
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}