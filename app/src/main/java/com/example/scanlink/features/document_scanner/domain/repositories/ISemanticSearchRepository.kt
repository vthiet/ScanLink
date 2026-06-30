package com.example.scanlink.features.document_scanner.domain.repositories

import com.example.scanlink.features.document_scanner.domain.entities.SearchResult

interface ISemanticSearchRepository {
    suspend fun loadModel(): Result<Unit>
    suspend fun generateEmbedding(text: String): Result<FloatArray>
    suspend fun indexDocument(documentId: String, pageNumber: Int, text: String): Result<Unit>
    suspend fun search(queryText: String, threshold: Float = 0.6f): Result<List<SearchResult>>
}
