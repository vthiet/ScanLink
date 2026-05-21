package com.example.scanlink.features.file_sharing.presentation.state

data class UploadUiState(

    val isLoading: Boolean = false,

    val successMessage: String? = null,

    val errorMessage: String? = null
)