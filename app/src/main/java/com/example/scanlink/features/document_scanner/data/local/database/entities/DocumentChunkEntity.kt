package com.example.scanlink.features.document_scanner.data.local.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "document_chunks",
    foreignKeys = [
        ForeignKey(
            entity = DocumentEntity::class,
            parentColumns = ["id"],
            childColumns = ["documentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["documentId"])]
)
data class DocumentChunkEntity(
    @PrimaryKey
    val id: String,                 // UUID ngẫu nhiên cho mỗi chunk
    val documentId: String,          // Khóa ngoại liên kết với DocumentEntity
    val pageNumber: Int,             // Số trang chứa chunk này
    val rawText: String,             // Nội dung chữ thô của chunk
    val embedding: FloatArray        // Mảng vector nhúng (384 chiều)
)
