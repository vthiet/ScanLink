package com.example.scanlink.features.file_sharing.presentation.state

import com.example.scanlink.features.file_sharing.presentation.model.DocumentUiModel

data class GalleryUiState(

    val isLoading: Boolean = false,

    val documents:
    List<DocumentUiModel> =
        emptyList(),

    val errorMessage: String? = null
)