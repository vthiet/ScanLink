package com.example.scanlink.features.file_sharing.data.mapper

import com.example.scanlink.features.file_sharing.data.local.entity.DocumentEntity
import com.example.scanlink.features.file_sharing.domain.model.Document

fun DocumentEntity.toDomain(): Document {

    return Document(
        id = id,
        title = title,
        storageUrl = storageUrl,
        localPath = localPath,
        fileSize = fileSize,
        extractedText = extractedText,
        isUploaded = isUploaded,
        createdAt = createdAt
    )
}

fun Document.toEntity(): DocumentEntity {

    return DocumentEntity(
        id = id,
        title = title,
        storageUrl = storageUrl,
        localPath = localPath,
        fileSize = fileSize,
        extractedText = extractedText,
        isUploaded = isUploaded,
        createdAt = createdAt
    )
}