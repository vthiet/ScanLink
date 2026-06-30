package com.example.scanlink.features.document_scanner.presentation.transfer

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.document_scanner.presentation.transfer.component.PrivateShareContent
import com.example.scanlink.features.document_scanner.presentation.transfer.component.PublicShareContent
import com.example.scanlink.features.document_scanner.presentation.transfer.component.TransferStatisticsCard
import com.example.scanlink.features.document_scanner.presentation.transfer.component.TransferTabBar
import com.example.scanlink.features.document_scanner.presentation.transfer.component.TransferTopBar
import com.example.scanlink.features.document_scanner.presentation.transfer.component.UploadContent
import com.example.scanlink.features.document_scanner.presentation.transfer.model.SharePermission
import com.example.scanlink.features.document_scanner.presentation.transfer.model.TransferTab
import com.example.scanlink.features.document_scanner.presentation.transfer.model.TransferUiState

@Composable
fun TransferContent(
    uiState: TransferUiState,
    snackbarHostState: SnackbarHostState,
    onTabSelected: (TransferTab) -> Unit,
    onUploadFile: (Uri) -> Unit,
    onRetryUpload: (String) -> Unit,
    onCancelUpload: (String) -> Unit,
    onPublicDocumentSelected: (String) -> Unit,
    onPublicPasswordChange: (String) -> Unit,
    onPublicExpireDaysChange: (String) -> Unit,
    onGeneratePublicLink: () -> Unit,
    onCopyPublicLink: (String) -> Unit,
    onDisablePublicLink: (String) -> Unit,
    onPrivateDocumentSelected: (String) -> Unit,
    onPrivateEmailChange: (String) -> Unit,
    onPrivatePermissionChange: (SharePermission) -> Unit,
    onSharePrivateAccess: () -> Unit,
    onRemovePrivateAccess: (String) -> Unit,
    onChangePrivatePermission: (String, SharePermission) -> Unit
) {
    val filePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let(onUploadFile)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TransferTopBar() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TransferStatisticsCard(
                    totalUploaded = uiState.totalUploaded,
                    publicLinks = uiState.publicLinks,
                    sharedUsers = uiState.sharedUsers
                )
            }
            item {
                TransferTabBar(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = onTabSelected
                )
            }
            item {
                AnimatedContent(
                    targetState = uiState.selectedTab,
                    label = "TransferTabContent"
                ) { tab ->
                    when (tab) {
                        TransferTab.Upload -> UploadContent(
                            state = uiState.uploadState,
                            onSelectFileClick = { filePicker.launch("*/*") },
                            onRetry = onRetryUpload,
                            onCancel = onCancelUpload
                        )

                        TransferTab.PublicShare -> PublicShareContent(
                            documents = uiState.documents,
                            state = uiState.publicShareState,
                            onDocumentSelected = onPublicDocumentSelected,
                            onPasswordChange = onPublicPasswordChange,
                            onExpireDaysChange = onPublicExpireDaysChange,
                            onGenerateLink = onGeneratePublicLink,
                            onCopyLink = onCopyPublicLink,
                            onDisableLink = onDisablePublicLink
                        )

                        TransferTab.PrivateShare -> PrivateShareContent(
                            documents = uiState.documents,
                            state = uiState.privateShareState,
                            onDocumentSelected = onPrivateDocumentSelected,
                            onEmailChange = onPrivateEmailChange,
                            onPermissionChange = onPrivatePermissionChange,
                            onShare = onSharePrivateAccess,
                            onRemoveAccess = onRemovePrivateAccess,
                            onChangePermission = onChangePrivatePermission
                        )
                    }
                }
            }
            item {
                Spacer(
                    modifier = Modifier.height(80.dp)
                )
            }
        }
    }
}
