package com.example.scanlink.features.document_scanner.data.repositories

import com.example.scanlink.features.document_scanner.data.local.database.dao.DocumentDao
import com.example.scanlink.features.document_scanner.data.local.database.entities.toDomain
import com.example.scanlink.features.document_scanner.data.local.database.entities.toEntity
import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page
import com.example.scanlink.features.document_scanner.domain.repositories.DocumentRepository
import com.example.scanlink.features.document_scanner.domain.repositories.IDocumentLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import kotlin.collections.map

class DocumentLocalRepositoryImpl @Inject constructor(
    private val documentDao: DocumentDao
) : IDocumentLocalRepository, DocumentRepository {

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

    override suspend fun renameDocument(documentId: String, title: String): Result<Unit> = runCatching {
        documentDao.renameDocument(documentId, title, System.currentTimeMillis())
        Unit
    }

    override suspend fun duplicateDocument(documentId: String): Result<Document?> = runCatching {
        val original = documentDao.getDocumentWithPagesById(documentId)?.toDomain() ?: return@runCatching null
        val newId = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val duplicated = original.copy(
            id = newId,
            title = "${original.title} Copy",
            createdAt = now,
            updatedAt = now,
            lastModified = now,
            pages = original.pages.map { page ->
                page.copy(
                    id = UUID.randomUUID().toString(),
                    documentId = newId,
                    createdAt = now
                )
            }
        )
        documentDao.saveDocumentWithPages(duplicated.toEntity(), duplicated.pages.map { it.toEntity() })
        duplicated
    }

    override fun getDocumentsFlow(ownerUid: String?): Flow<List<Document>> {
        return documentDao.getAllDocumentsWithPages(ownerUid).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getDocumentById(documentId: String): Result<Document?> = runCatching {
        documentDao.getDocumentWithPagesById(documentId)?.toDomain()
    }

    override suspend fun associateGuestDocuments(ownerUid: String): Result<Unit> = runCatching {
        documentDao.associateGuestDocuments(ownerUid)
        Unit
    }
}
