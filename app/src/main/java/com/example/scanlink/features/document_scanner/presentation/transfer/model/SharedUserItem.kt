package com.example.scanlink.features.document_scanner.presentation.transfer.model

data class SharedUserItem(
    val id: String,
    val documentId: String,
    val documentName: String,
    val email: String,
    val permission: SharePermission,
    val sharedDate: String
)
