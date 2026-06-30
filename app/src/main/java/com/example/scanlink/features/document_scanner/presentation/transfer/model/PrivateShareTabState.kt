package com.example.scanlink.features.document_scanner.presentation.transfer.model

data class PrivateShareTabState(
    val selectedDocumentId: String? = null,
    val email: String = "",
    val permission: SharePermission = SharePermission.Viewer,
    val isSharing: Boolean = false,
    val sharedUsers: List<SharedUserItem> = emptyList()
)
