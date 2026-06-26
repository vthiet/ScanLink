package com.example.scanlink.features.document_scanner.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey
    val id: String,
    val ownerUid: String?,
    val title: String,
    val storageUrl: String?,
    val fileSize: Long,
    val extractedText: String?,
    val pdfPath: String?,
    val createdAt: Long,
    val updatedAt: Long,
    val isSynced: Boolean = false
)
