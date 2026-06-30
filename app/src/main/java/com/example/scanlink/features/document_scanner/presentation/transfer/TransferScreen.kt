package com.example.scanlink.features.document_scanner.presentation.transfer

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TransferScreen(
    viewModel: TransferViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.consumeActionMessage()
        }
    }

    TransferContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onTabSelected = viewModel::selectTab,
        onUploadFile = viewModel::uploadFile,
        onRetryUpload = viewModel::retryUpload,
        onCancelUpload = viewModel::cancelUpload,
        onPublicDocumentSelected = viewModel::selectPublicDocument,
        onPublicPasswordChange = viewModel::updatePublicPassword,
        onPublicExpireDaysChange = viewModel::updatePublicExpireDays,
        onGeneratePublicLink = viewModel::generatePublicLink,
        onCopyPublicLink = viewModel::copyPublicLink,
        onDisablePublicLink = viewModel::disablePublicLink,
        onPrivateDocumentSelected = viewModel::selectPrivateDocument,
        onPrivateEmailChange = viewModel::updatePrivateEmail,
        onPrivatePermissionChange = viewModel::updatePrivatePermission,
        onSharePrivateAccess = viewModel::sharePrivateAccess,
        onRemovePrivateAccess = viewModel::removePrivateAccess,
        onChangePrivatePermission = viewModel::changePrivatePermission
    )
}
