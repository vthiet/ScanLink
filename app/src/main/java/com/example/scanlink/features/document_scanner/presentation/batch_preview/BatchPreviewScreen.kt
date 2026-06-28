package com.example.scanlink.features.document_scanner.presentation.batch_preview

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanlink.features.document_scanner.presentation.camera.CameraViewModel
import com.example.scanlink.features.document_scanner.presentation.preview.PreviewViewModel

@Composable
fun BatchPreviewScreen(
    viewModel: CameraViewModel,
    onBackClick: () -> Unit,
    onContinueScanning: () -> Unit,
    onRetakePage: () -> Unit,
    onExportComplete: (String) -> Unit,
    previewViewModel: PreviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val previewState by previewViewModel.uiState.collectAsStateWithLifecycle()
    var selectedUri by remember(state.capturedImages) {
        mutableStateOf(state.capturedImages.firstOrNull().orEmpty())
    }

    LaunchedEffect(state.capturedImages) {
        if (selectedUri !in state.capturedImages) {
            selectedUri = state.capturedImages.firstOrNull().orEmpty()
        }
    }

    LaunchedEffect(selectedUri) {
        if (selectedUri.isNotBlank()) {
            previewViewModel.setImageUri(selectedUri)
        }
    }

    LaunchedEffect(state.uiState) {
        val error = state.uiState as? com.example.scanlink.features.document_scanner.presentation.camera.CameraUiState.Error
        error?.let { Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show() }
    }

    BatchPreviewContent(
        imageUris = state.capturedImages,
        selectedUri = selectedUri,
        previewState = previewState,
        isExporting = state.isLoading,
        onBackClick = onBackClick,
        onSelectPage = { selectedUri = it },
        onDeletePage = {
            viewModel.removeCapturedImage(selectedUri)
        },
        onRetakePage = {
            if (selectedUri.isNotBlank()) {
            viewModel.removeCapturedImage(selectedUri)
            }
            onRetakePage()
        },
        onContinueScanning = onContinueScanning,
        onRotatePage = previewViewModel::rotateRight,
        onCropPage = previewViewModel::toggleCropMode,
        onCropRectChange = previewViewModel::updateCropRect,
        onApplyCrop = {
            val oldUri = selectedUri
            previewViewModel.applyCrop { savedUri ->
                viewModel.replaceCapturedImage(oldUri, savedUri)
                selectedUri = savedUri
            }
        },
        onFilterSelected = previewViewModel::onFilterSelected,
        onExportPdf = {
            if (selectedUri.isBlank()) {
                viewModel.exportCapturedImagesAsPdf(context) { documentId ->
                    viewModel.clearCapturedImages()
                    onExportComplete(documentId)
                }
            } else {
                val oldUri = selectedUri
                previewViewModel.saveImage { savedUri ->
                    viewModel.replaceCapturedImage(oldUri, savedUri)
                    selectedUri = savedUri
                    viewModel.exportCapturedImagesAsPdf(context) { documentId ->
                        viewModel.clearCapturedImages()
                        onExportComplete(documentId)
                    }
                }
            }
        }
    )
}
