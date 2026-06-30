package com.example.scanlink.features.document_scanner.presentation.file_detail

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.document_scanner.presentation.file_detail.components.BottomActionBar
import com.example.scanlink.features.document_scanner.presentation.file_detail.components.DeleteConfirmationDialog
import com.example.scanlink.features.document_scanner.presentation.file_detail.components.ErrorState
import com.example.scanlink.features.document_scanner.presentation.file_detail.components.FileInformationCard
import com.example.scanlink.features.document_scanner.presentation.file_detail.components.FilePreviewCard
import com.example.scanlink.features.document_scanner.presentation.file_detail.components.LoadingSkeleton
import com.example.scanlink.features.document_scanner.presentation.file_detail.components.OcrPreviewCard
import com.example.scanlink.features.document_scanner.presentation.file_detail.components.QuickActionGrid
import com.example.scanlink.features.document_scanner.presentation.file_detail.components.RenameDialog
import com.example.scanlink.features.document_scanner.presentation.transfer.component.BottomSheetShare
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDetailContent(
    uiState: FileDetailUiState,
    onBackClick: () -> Unit,
    onRetry: () -> Unit,
    onRenameClick: () -> Unit,
    onRenameValueChange: (String) -> Unit,
    onDismissRename: () -> Unit,
    onConfirmRename: () -> Unit,
    onDeleteClick: () -> Unit,
    onDismissDelete: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDuplicateClick: () -> Unit,
    onConvertClick: () -> Unit,
    onPrintClick: () -> Unit,
    onCopyOcrClick: (Context) -> Unit,
    onShareClick: () -> Unit,
    onDismissShareOptions: () -> Unit,
    onSystemShareClick: (Context) -> Unit,
    onPublicLinkClick: () -> Unit,
    onPrivateAccessClick: () -> Unit,
    onExportPdfClick: (Context) -> Unit,
    onExportImageClick: (Context) -> Unit,
    onExtractTextClick: () -> Unit,
    onConsumeActionMessage: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            onConsumeActionMessage()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                title = {
                    Text(
                        text = uiState.document?.title ?: "File Detail",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isMenuExpanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                    DropdownMenu(
                        expanded = isMenuExpanded,
                        onDismissRequest = { isMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Rename") },
                            leadingIcon = { Icon(Icons.Default.Edit, null) },
                            onClick = {
                                isMenuExpanded = false
                                onRenameClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Share") },
                            leadingIcon = { Icon(Icons.Default.Share, null) },
                            onClick = {
                                isMenuExpanded = false
                                onShareClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Extract Text (OCR)") },
                            leadingIcon = { Icon(Icons.Default.Article, null) },
                            onClick = {
                                isMenuExpanded = false
                                onExtractTextClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export PDF") },
                            leadingIcon = { Icon(Icons.Default.PictureAsPdf, null) },
                            onClick = {
                                isMenuExpanded = false
                                onExportPdfClick(context)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export Image") },
                            leadingIcon = { Icon(Icons.Default.Image, null) },
                            onClick = {
                                isMenuExpanded = false
                                onExportImageClick(context)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            onClick = {
                                isMenuExpanded = false
                                onDeleteClick()
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            if (uiState.document != null) {
                BottomActionBar(
                    onShareClick = onShareClick,
                    onSaveToDeviceClick = {
                        scope.launch { snackbarHostState.showSnackbar("Saved to device") }
                    }
                )
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingSkeleton(Modifier.padding(paddingValues))
            uiState.errorMessage != null && uiState.document == null -> ErrorState(
                message = uiState.errorMessage,
                onRetry = onRetry,
                modifier = Modifier.padding(paddingValues)
            )
            uiState.document != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { FilePreviewCard(document = uiState.document) }
                    item { FileInformationCard(document = uiState.document) }
                    item {
                        OcrPreviewCard(
                            text = uiState.document.extractedText,
                            onCopyClick = { onCopyOcrClick(context) },
                            onEditClick = onRenameClick,
                            onViewAllClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(uiState.document.extractedText.orEmpty())
                                }
                            },
                            onExtractTextClick = onExtractTextClick
                        )
                    }
                    item {
                        QuickActionGrid(
                            onShareClick = onShareClick,
                            onRenameClick = onRenameClick,
                            onDuplicateClick = onDuplicateClick,
                            onConvertClick = onConvertClick,
                            onPrintClick = onPrintClick,
                            onDeleteClick = onDeleteClick
                        )
                    }
                    item { Spacer(Modifier.height(72.dp)) }
                }
            }
        }
    }

    if (uiState.isRenameDialogVisible) {
        RenameDialog(
            value = uiState.renameValue,
            onValueChange = onRenameValueChange,
            onDismiss = onDismissRename,
            onConfirm = onConfirmRename
        )
    }

    if (uiState.isDeleteDialogVisible) {
        DeleteConfirmationDialog(
            onDismiss = onDismissDelete,
            onConfirm = onConfirmDelete
        )
    }

    if (uiState.isShareOptionsVisible) {
        BottomSheetShare(
            onDismiss = onDismissShareOptions,
            onSystemShare = {
                onDismissShareOptions()
                onSystemShareClick(it)
            },
            onPublicLink = onPublicLinkClick,
            onPrivateShare = onPrivateAccessClick,
            context = context
        )
    }

}
