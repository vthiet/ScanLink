package com.example.scanlink.features.document_scanner.data.local.database.entities

import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page

fun DocumentEntity.toDomain(pages: List<PageEntity>): Document {
    return Document(
        id = id,
        ownerUid = ownerUid,
        title = title,
        storageUrl = storageUrl,
        fileSize = fileSize,
        extractedText = extractedText,
        pdfPath = pdfPath,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced,
        pageCount = pageCount,
        mimeType = mimeType,
        thumbnailPath = thumbnailPath,
        lastModified = lastModified,
        pages = pages.map { it.toDomain() }
    )
}

fun DocumentWithPages.toDomain(): Document {
    return document.toDomain(pages)
}

fun PageEntity.toDomain(): Page {
    return Page(
        id = id,
        documentId = documentId,
        pageNumber = pageNumber,
        imagePath = imagePath,
        ocrText = ocrText,
        createdAt = createdAt
    )
}

fun Document.toEntity(): DocumentEntity {
    return DocumentEntity(
        id = id,
        ownerUid = ownerUid,
        title = title,
        storageUrl = storageUrl,
        fileSize = fileSize,
        extractedText = extractedText,
        pdfPath = pdfPath,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isSynced = isSynced,
        pageCount = pageCount,
        mimeType = mimeType,
        thumbnailPath = thumbnailPath,
        lastModified = lastModified
    )
}

fun Page.toEntity(): PageEntity {
    return PageEntity(
        id = id,
        documentId = documentId,
        pageNumber = pageNumber,
        imagePath = imagePath,
        ocrText = ocrText,
        createdAt = createdAt
    )
}
