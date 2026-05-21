package com.example.scanlink.features.file_sharing.domain.repository

import com.example.scanlink.features.file_sharing.domain.model.Document
import java.io.File

interface FileSharingRepository {

    suspend fun getLocalDocuments(): List<Document>

    suspend fun uploadDocument(
        file: File
    ): Result<Document>

    suspend fun createShareLink(
        documentId: String
    ): Result<String>

    suspend fun saveDocument(
        document: Document
    )

    suspend fun getPendingUploads(): List<Document>

    suspend fun syncPendingDocuments()


}