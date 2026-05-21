package com.example.scanlink.features.file_sharing.presentation.model

data class DocumentUiModel(

    val id: String,

    val title: String,

    val fileSize: String,

    val uploadStatus: String,

    val storageUrl: String?
)