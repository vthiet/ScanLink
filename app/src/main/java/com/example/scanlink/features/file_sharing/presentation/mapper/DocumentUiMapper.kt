package com.example.scanlink.features.file_sharing.presentation.mapper

import com.example.scanlink.features.file_sharing.domain.model.Document
import com.example.scanlink.features.file_sharing.presentation.model.DocumentUiModel

fun Document.toUiModel():
        DocumentUiModel {

    val status =
        when {

            syncing -> "Syncing"

            isUploaded -> "Uploaded"

            else -> "Pending"
        }

    return DocumentUiModel(
        id = id,
        title = title,
        fileSize =
            "${fileSize / 1024} KB",
        uploadStatus = status,
        storageUrl = storageUrl
    )
}