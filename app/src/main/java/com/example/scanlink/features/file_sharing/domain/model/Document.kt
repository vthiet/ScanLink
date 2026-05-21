package com.example.scanlink.features.file_sharing.domain.model

data class Document(

    val id: String,

    val title: String,

    val storageUrl: String?,

    val localPath: String?,

    val fileSize: Long,

    val syncing: Boolean = false,

    val extractedText: String?,

    val isUploaded: Boolean,

    val createdAt: Long
)