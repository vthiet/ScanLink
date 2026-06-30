package com.example.scanlink.features.document_scanner.domain.entities

data class SearchResult(
    val documentId: String,
    val documentTitle: String,
    val pageNumber: Int,
    val snippet: String,
    val score: Float
)
