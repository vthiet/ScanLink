package com.example.scanlink.features.document_scanner.presentation.transfer.model

data class TransferUiState(
    val selectedTab: TransferTab = TransferTab.Upload,
    val documents: List<TransferDocumentOption> = emptyList(),
    val uploadState: UploadTabState = UploadTabState(),
    val publicShareState: PublicShareTabState = PublicShareTabState(),
    val privateShareState: PrivateShareTabState = PrivateShareTabState(),
    val actionMessage: String? = null
) {
    val totalUploaded: Int get() = uploadState.items.count { it.state == UploadState.Synced }
    val publicLinks: Int get() = publicShareState.links.count { it.isEnabled }
    val sharedUsers: Int get() = privateShareState.sharedUsers.size
}
