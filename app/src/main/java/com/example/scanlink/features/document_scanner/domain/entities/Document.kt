package com.example.scanlink.features.document_scanner.domain.entities

data class Document(
    val id: String,
    val ownerUid: String?,
    val title: String,
    val storageUrl: String?,
    val fileSize: Long,
    val extractedText: String?,
    val pdfPath: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false,
    val pageCount: Int = 0,
    val mimeType: String? = null,
    val thumbnailPath: String? = null,
    val lastModified: Long? = null,
    val pages: List<Page> = emptyList()
)
