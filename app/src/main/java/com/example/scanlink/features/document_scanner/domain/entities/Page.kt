package com.example.scanlink.features.document_scanner.domain.entities

data class Page(
    val id: String,
    val documentId: String,
    val pageNumber: Int,
    val imagePath: String,
    val ocrText: String?,
    val createdAt: Long
)
