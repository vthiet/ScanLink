package com.example.scanlink.features.document_scanner.domain.repositories

import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page
import kotlinx.coroutines.flow.Flow

interface IDocumentLocalRepository {
    suspend fun saveDocument(document: Document, pages: List<Page>): Result<Unit>
    suspend fun deleteDocument(documentId: String): Result<Unit>
    suspend fun renameDocument(documentId: String, title: String): Result<Unit>
    suspend fun duplicateDocument(documentId: String): Result<Document?>
    fun getDocumentsFlow(ownerUid: String?): Flow<List<Document>>
    suspend fun getDocumentById(documentId: String): Result<Document?>
    suspend fun associateGuestDocuments(ownerUid: String): Result<Unit>
}

interface DocumentRepository : IDocumentLocalRepository
