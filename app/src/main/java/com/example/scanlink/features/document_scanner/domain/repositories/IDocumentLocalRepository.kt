package com.example.scanlink.features.document_scanner.domain.repositories

import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page
import kotlinx.coroutines.flow.Flow

interface IDocumentLocalRepository {
    suspend fun saveDocument(document: Document, pages: List<Page>): Result<Unit>
    suspend fun deleteDocument(documentId: String): Result<Unit>
    fun getDocumentsFlow(): Flow<List<Document>>
    suspend fun getDocumentById(documentId: String): Result<Document?>
}
