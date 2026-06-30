package com.example.scanlink.features.dashboard.presentation.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanlink.core.ui.components.card.RecentFileItem
import com.example.scanlink.core.ui.components.header.AppHeader
import com.example.scanlink.core.ui.model.FileType
import com.example.scanlink.core.ui.model.RecentFile
import com.example.scanlink.core.ui.model.home.QuickAction
import com.example.scanlink.features.dashboard.presentation.home.components.QuickActionSection
import com.example.scanlink.features.dashboard.presentation.home.components.RecentSection

@Composable
fun HomeContent(
    paddingValues: PaddingValues = PaddingValues(0.dp),
    viewModel: HomeViewModel = hiltViewModel(),
    onFileClick: (String) -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            viewModel.createPdfFromUris(context, uris)
        }
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val quickActions = remember { homeQuickActions }
    val documents = uiState.documents

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

        Spacer(modifier = Modifier.height(12.dp))

        QuickActionSection(
            actions = quickActions,
            onActionClick = { action ->
                when (action.title) {
                    HomeAction.ImportImage.title -> launcher.launch("image/*")
                }
            }
        )

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
                    RecentFileItem(
                        file = file,
                        onClick = { onFileClick(file.id) }
                    )
                }
            }
        }
    }
}

private enum class HomeAction(val title: String) {
    SmartScan("Smart Scan"),
    IdCards("ID Cards"),
    PdfTools("PDF Tools"),
    ImportImage("Import IMG"),
    Translate("Translate"),
    AllTools("All Tools")
}

private val homeQuickActions = listOf(
    QuickAction(
        title = HomeAction.SmartScan.title,
        icon = Icons.Default.Description,
        description = "Scan document"
    ),
    QuickAction(
        title = HomeAction.IdCards.title,
        icon = Icons.Default.Badge,
        description = "ID capture"
    ),
    QuickAction(
        title = HomeAction.PdfTools.title,
        icon = Icons.Default.PictureAsPdf,
        description = "PDF toolkit"
    ),
    QuickAction(
        title = HomeAction.ImportImage.title,
        icon = Icons.Default.Image,
        description = "Import photos"
    ),
    QuickAction(
        title = HomeAction.Translate.title,
        icon = Icons.Default.Translate,
        description = "Translate text"
    ),
    QuickAction(
        title = HomeAction.AllTools.title,
        icon = Icons.Default.GridView,
        description = "More actions"
    )
)

private val sampleRecentFiles = listOf(
    RecentFile(
        id = "1",
        name = "ScanLink_Project_Brief.docx",
        sizeLabel = "820 KB",
        type = FileType.DOCX,
        createdAt = "2026/04/16"
    ),
    RecentFile(
        id = "2",
        name = "Mobile_Final_Report.pdf",
        sizeLabel = "1.8 MB",
        type = FileType.PDF,
        createdAt = "2026/04/16"
    ),
    RecentFile(
        id = "3",
        name = "Contract_Scan.pdf",
        sizeLabel = "3.1 MB",
        type = FileType.PDF,
        createdAt = "2026/04/16"
    ),
    RecentFile(
        id = "4",
        name = "Receipt_Archive.pdf",
        sizeLabel = "2.4 MB",
        type = FileType.PDF,
        createdAt = "2026/04/16"
    )
)
