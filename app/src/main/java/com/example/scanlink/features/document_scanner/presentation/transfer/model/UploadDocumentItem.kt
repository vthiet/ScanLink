package com.example.scanlink.features.document_scanner.presentation.transfer.model

data class UploadDocumentItem(
    val id: String,
    val name: String,
    val sizeLabel: String,
    val thumbnailPath: String?,
    val progress: Float,
    val state: UploadState
)
