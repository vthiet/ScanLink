package com.example.scanlink.features.document_scanner.presentation.transfer.model

data class PublicLinkItem(
    val id: String,
    val documentId: String,
    val documentName: String,
    val url: String,
    val createdDate: String,
    val expireDate: String?,
    val passwordEnabled: Boolean,
    val isEnabled: Boolean = true
)
