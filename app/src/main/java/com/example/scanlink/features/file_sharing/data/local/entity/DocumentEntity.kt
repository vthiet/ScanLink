package com.example.scanlink.features.file_sharing.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "documents")
data class DocumentEntity(

    @PrimaryKey
    val id: String,

    val title: String,

    val storageUrl: String?,

    val localPath: String?,

    val fileSize: Long,

    val extractedText: String?,

    val isUploaded: Boolean,

    val createdAt: Long
)