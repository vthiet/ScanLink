package com.example.scanlink.features.document_scanner.data.repositories

import com.example.scanlink.features.document_scanner.data.local.database.dao.DocumentDao
import com.example.scanlink.features.document_scanner.data.local.database.entities.toDomain
import com.example.scanlink.features.document_scanner.data.local.database.entities.toEntity
import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page
import com.example.scanlink.features.document_scanner.domain.repositories.IDocumentLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.collections.map

class DocumentLocalRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao
) : IDocumentLocalRepository {

    override suspend fun saveDocument(document: Document, pages: List<Page>): Result<Unit> = runCatching {
        val documentEntity = document.toEntity()
        val pageEntities = pages.map { it.toEntity() }
        documentDao.saveDocumentWithPages(documentEntity, pageEntities)
        Unit
    }

    override suspend fun deleteDocument(documentId: String): Result<Unit> = runCatching {
        documentDao.deleteDocumentById(documentId)
        Unit
    }

    override fun getDocumentsFlow(): Flow<List<Document>> {
        return documentDao.getAllDocumentsWithPages().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getDocumentById(documentId: String): Result<Document?> = runCatching {
        documentDao.getDocumentWithPagesById(documentId)?.toDomain()
    }
}
