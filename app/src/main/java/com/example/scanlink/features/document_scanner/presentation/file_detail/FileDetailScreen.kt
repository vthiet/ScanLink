package com.example.scanlink.features.document_scanner.presentation.file_detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanlink.core.ui.theme.ScanLinkTheme

@Composable
fun FileDetailScreen(
    onBackClick: () -> Unit,
    viewModel: FileDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FileDetailContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onRetry = viewModel::loadDocument,
        onRenameClick = viewModel::showRenameDialog,
        onRenameValueChange = viewModel::onRenameValueChange,
        onDismissRename = viewModel::hideRenameDialog,
        onConfirmRename = viewModel::confirmRename,
        onDeleteClick = viewModel::showDeleteDialog,
        onDismissDelete = viewModel::hideDeleteDialog,
        onConfirmDelete = { viewModel.confirmDelete(onBackClick) },
        onDuplicateClick = viewModel::duplicateDocument,
        onConvertClick = viewModel::convertDocument,
        onPrintClick = viewModel::printDocument,
        onCopyOcrClick = viewModel::copyOcrText,
        onShareClick = viewModel::shareDocument,
        onExportPdfClick = viewModel::exportPdf,
        onExportImageClick = viewModel::exportImage,
        onConsumeActionMessage = viewModel::consumeActionMessage
    )
}


