package com.example.scanlink.features.file_sharing.domain.usecase

import com.example.scanlink.features.file_sharing.domain.model.Document
import com.example.scanlink.features.file_sharing.domain.repository.FileSharingRepository

class GetDocumentsUseCase(
    private val repository:
    FileSharingRepository
) {

    suspend operator fun invoke() =
        repository.getLocalDocuments()
}