package com.example.scanlink.features.document_scanner.presentation.transfer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: TransferViewModel = hiltViewModel(),
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.uploadFile(uri)
        }
    }

    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_SHORT).show()
            viewModel.consumeActionMessage()
        }
    }

    var selectedTab by remember { mutableStateOf(TransferTab.Upload) }

    val uploadingFiles = uiState.uploadingFiles
    val recentFiles = uiState.recentFiles

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(paddingValues)
    ) {
        AppHeader(
            showSearchBar = true,
            onAvatarClick = onProfileClick
        )

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
                UploadAreaCard(onSelectFileClick = { launcher.launch("*/*") })
            }

            // Đang tải lên
            if (uploadingFiles.isNotEmpty()) {
                item {
                    Text(
                        text = "ĐANG TẢI LÊN",
                        color = MaterialTheme.colorScheme.onBackground,
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
                    color = MaterialTheme.colorScheme.onBackground,
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
