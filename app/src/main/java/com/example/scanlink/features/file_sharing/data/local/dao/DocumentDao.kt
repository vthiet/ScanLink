package com.example.scanlink.features.file_sharing.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.scanlink.features.file_sharing.data.local.entity.DocumentEntity

@Dao
interface DocumentDao {

    @Query("""
        SELECT * FROM documents
        ORDER BY createdAt DESC
    """)
    suspend fun getAllDocuments():
            List<DocumentEntity>

    @Insert(
        onConflict =
            OnConflictStrategy.REPLACE
    )
    suspend fun insertDocument(
        document: DocumentEntity
    )

    @Query("""
        SELECT * FROM documents
        WHERE isUploaded = 0
    """)
    suspend fun getPendingUploads():
            List<DocumentEntity>

    @Query("""
        DELETE FROM documents
        WHERE id = :documentId
    """)
    suspend fun deleteDocument(
        documentId: String
    )
    //
        @Query("""
        UPDATE documents
        SET
            isUploaded = 1,
            storageUrl = :storageUrl
        WHERE id = :documentId
    """)
        suspend fun markAsUploaded(
            documentId: String,
            storageUrl: String
        )
}