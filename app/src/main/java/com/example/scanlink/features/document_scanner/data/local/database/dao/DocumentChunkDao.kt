package com.example.scanlink.features.document_scanner.data.local.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scanlink.features.document_scanner.data.local.database.entities.DocumentChunkEntity

@Dao
interface DocumentChunkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChunks(chunks: List<DocumentChunkEntity>): List<Long>

    @Query("DELETE FROM document_chunks WHERE documentId = :documentId")
    suspend fun deleteChunksByDocumentId(documentId: String): Int

    @Query("SELECT * FROM document_chunks")
    suspend fun getAllChunks(): List<DocumentChunkEntity>
}
