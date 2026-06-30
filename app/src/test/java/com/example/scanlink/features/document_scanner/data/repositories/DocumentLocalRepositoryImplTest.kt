package com.example.scanlink.features.document_scanner.data.repositories

import com.example.scanlink.features.document_scanner.data.local.database.dao.DocumentDao
import com.example.scanlink.features.document_scanner.data.local.database.entities.DocumentEntity
import com.example.scanlink.features.document_scanner.data.local.database.entities.DocumentWithPages
import com.example.scanlink.features.document_scanner.data.local.database.entities.PageEntity
import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page
import com.example.scanlink.features.document_scanner.domain.entities.SearchResult
import com.example.scanlink.features.document_scanner.domain.repositories.ISemanticSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DocumentLocalRepositoryImplTest {

    // Simple fake DocumentDao
    private class FakeDocumentDao : DocumentDao {
        var savedDocument: DocumentEntity? = null
        var savedPages: List<PageEntity>? = null
        var deletedDocumentId: String? = null

        override suspend fun insertDocument(document: DocumentEntity): Long = 0L
        override suspend fun insertPages(pages: List<PageEntity>): List<Long> = emptyList()

        override suspend fun saveDocumentWithPages(document: DocumentEntity, pages: List<PageEntity>): Boolean {
            savedDocument = document
            savedPages = pages
            return true
        }

        override suspend fun deletePagesForDocument(documentId: String): Int = 0
        override suspend fun deleteDocument(document: DocumentEntity): Int = 0
        override suspend fun deleteDocumentById(documentId: String): Int {
            deletedDocumentId = documentId
            return 1
        }

        override suspend fun renameDocument(documentId: String, title: String, updatedAt: Long): Int = 0
        override fun getAllDocumentsWithPages(ownerUid: String?): Flow<List<DocumentWithPages>> = emptyFlow()
        override suspend fun associateGuestDocuments(ownerUid: String): Int = 0
        override suspend fun getDocumentWithPagesById(documentId: String): DocumentWithPages? = null
        override fun getAllDocuments(): Flow<List<DocumentEntity>> = emptyFlow()
        override fun getPagesForDocument(documentId: String): Flow<List<PageEntity>> = emptyFlow()
    }

    // Fake ISemanticSearchRepository tracking calls
    private class FakeSemanticSearchRepository : ISemanticSearchRepository {
        val indexedPages = mutableListOf<Triple<String, Int, String>>()
        val clearedDocuments = mutableListOf<String>()

        override suspend fun loadModel(): Result<Unit> = Result.success(Unit)
        override suspend fun generateEmbedding(text: String): Result<FloatArray> = Result.success(FloatArray(384))
        
        override suspend fun indexDocument(documentId: String, pageNumber: Int, text: String): Result<Unit> {
            indexedPages.add(Triple(documentId, pageNumber, text))
            return Result.success(Unit)
        }

        override suspend fun search(queryText: String, threshold: Float): Result<List<SearchResult>> = Result.success(emptyList())

        override suspend fun clearIndexForDocument(documentId: String): Result<Unit> {
            clearedDocuments.add(documentId)
            return Result.success(Unit)
        }
    }

    @Test
    fun testSaveDocumentTriggersIndexingForPagesWithOcrText() = runBlocking {
        val fakeDao = FakeDocumentDao()
        val fakeSearchRepo = FakeSemanticSearchRepository()
        val repository = DocumentLocalRepositoryImpl(fakeDao, fakeSearchRepo)

        val document = Document(
            id = "doc123",
            ownerUid = null,
            title = "Test Doc",
            storageUrl = null,
            fileSize = 100L,
            extractedText = "extracted text page 1",
            pdfPath = null,
            createdAt = 1000L,
            updatedAt = 1000L
        )

        val pages = listOf(
            Page(id = "p1", documentId = "doc123", pageNumber = 1, imagePath = "path1", ocrText = "OCR Text 1", createdAt = 1000L),
            Page(id = "p2", documentId = "doc123", pageNumber = 2, imagePath = "path2", ocrText = "", createdAt = 1000L),
            Page(id = "p3", documentId = "doc123", pageNumber = 3, imagePath = "path3", ocrText = null, createdAt = 1000L)
        )

        val result = repository.saveDocument(document, pages)

        assertTrue(result.isSuccess)
        // Verify it was saved to DB
        assertEquals("doc123", fakeDao.savedDocument?.id)
        assertEquals(3, fakeDao.savedPages?.size)

        // Verify index was cleared
        assertEquals(1, fakeSearchRepo.clearedDocuments.size)
        assertEquals("doc123", fakeSearchRepo.clearedDocuments[0])

        // Verify only pages with non-blank OCR text were indexed (only page 1)
        assertEquals(1, fakeSearchRepo.indexedPages.size)
        val indexedPage = fakeSearchRepo.indexedPages[0]
        assertEquals("doc123", indexedPage.first)
        assertEquals(1, indexedPage.second)
        assertEquals("OCR Text 1", indexedPage.third)
    }
}
