package com.example.scanlink.features.document_scanner.data.local.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.scanlink.features.document_scanner.data.local.database.entities.DocumentEntity
import com.example.scanlink.features.document_scanner.data.local.database.entities.DocumentWithPages
import com.example.scanlink.features.document_scanner.data.local.database.entities.PageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertDocument(document: DocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertPages(pages: List<PageEntity>): List<Long>

    @Transaction
    suspend fun saveDocumentWithPages(document: DocumentEntity, pages: List<PageEntity>): Boolean {
        insertDocument(document)
        // Delete existing pages for this document first if updating
        deletePagesForDocument(document.id)
        insertPages(pages)
        return true
    }

    @Query("DELETE FROM pages WHERE documentId = :documentId")
    suspend fun deletePagesForDocument(documentId: String): Int

    @Delete
    suspend fun deleteDocument(document: DocumentEntity): Int

    @Query("DELETE FROM documents WHERE id = :documentId")
    suspend fun deleteDocumentById(documentId: String): Int

    @Transaction
    @Query("SELECT * FROM documents ORDER BY updatedAt DESC")
    fun getAllDocumentsWithPages(): Flow<List<DocumentWithPages>>

    @Transaction
    @Query("SELECT * FROM documents WHERE id = :documentId")
    suspend fun getDocumentWithPagesById(documentId: String): DocumentWithPages?

    @Query("SELECT * FROM documents ORDER BY updatedAt DESC")
    fun getAllDocuments(): Flow<List<DocumentEntity>>

    @Query("SELECT * FROM pages WHERE documentId = :documentId ORDER BY pageNumber ASC")
    fun getPagesForDocument(documentId: String): Flow<List<PageEntity>>
}