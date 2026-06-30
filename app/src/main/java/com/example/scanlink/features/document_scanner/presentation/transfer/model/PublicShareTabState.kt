package com.example.scanlink.features.document_scanner.presentation.transfer.model

data class PublicShareTabState(
    val selectedDocumentId: String? = null,
    val password: String = "",
    val expireDays: String = "",
    val isGenerating: Boolean = false,
    val links: List<PublicLinkItem> = emptyList()
)
