package com.example.scanlink.features.document_scanner.presentation.batch_preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.document_scanner.domain.entities.CropRect
import com.example.scanlink.features.document_scanner.domain.entities.ScanFilterType
import com.example.scanlink.features.document_scanner.presentation.ocr.components.FilterSelector
import com.example.scanlink.features.document_scanner.presentation.preview.PreviewUiState
import com.example.scanlink.features.document_scanner.presentation.preview.components.PreviewImageViewer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchPreviewContent(
    imageUris: List<String>,
    selectedUri: String,
    previewState: PreviewUiState,
    isExporting: Boolean,
    onBackClick: () -> Unit,
    onSelectPage: (String) -> Unit,
    onDeletePage: () -> Unit,
    onRetakePage: () -> Unit,
    onContinueScanning: () -> Unit,
    onRotatePage: () -> Unit,
    onCropPage: () -> Unit,
    onCropRectChange: (CropRect) -> Unit,
    onApplyCrop: () -> Unit,
    onFilterSelected: (ScanFilterType) -> Unit,
    onExportPdf: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = WindowInsets(0),
                title = { Text(text = "Review Pages") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (selectedUri.isNotBlank()) {
                        if (previewState.cropMode) {
                            IconButton(
                                onClick = onCropPage,
                                enabled = !isExporting
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancel crop"
                                )
                            }
                            IconButton(
                                onClick = onApplyCrop,
                                enabled = !isExporting
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Apply crop"
                                )
                            }
                        } else {
                            IconButton(
                                onClick = onExportPdf,
                                enabled = !isExporting
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Done"
                                )
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            BatchBottomBar(
                canActOnPage = selectedUri.isNotBlank(),
                cropMode = previewState.cropMode,
                isExporting = isExporting,
                onDeletePage = onDeletePage,
                onRetakePage = onRetakePage,
                onContinueScanning = onContinueScanning,
                onRotatePage = onRotatePage,
                onCropPage = onCropPage
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedUri.isNotBlank()) {
                        PreviewImageViewer(
                            previewBitmap = previewState.previewBitmap,
                            imageUri = previewState.imageUri.ifBlank { selectedUri },
                            rotation = previewState.rotation,
                            flipHorizontal = previewState.flipHorizontal,
                            flipVertical = previewState.flipVertical,
                            cropMode = previewState.cropMode,
                            cropRect = previewState.cropRect,
                            onCropRectChange = onCropRectChange,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = "No pages",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (!previewState.cropMode && selectedUri.isNotBlank()) {
                    FilterSelector(
                        selectedFilter = previewState.selectedFilter,
                        onFilterSelected = onFilterSelected
                    )
                }

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(104.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    itemsIndexed(imageUris) { index, uri ->
                        BatchPageItem(
                            imageUri = uri,
                            pageNumber = index + 1,
                            selected = uri == selectedUri,
                            onClick = { onSelectPage(uri) }
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    }
                }
            }

            if (isExporting) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.45f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
