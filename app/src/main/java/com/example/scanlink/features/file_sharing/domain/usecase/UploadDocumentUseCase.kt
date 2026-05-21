package com.example.scanlink.features.file_sharing.domain.usecase

import com.example.scanlink.features.file_sharing.domain.repository.FileSharingRepository
import java.io.File

class UploadDocumentUseCase(
    private val repository: FileSharingRepository
) {

    suspend operator fun invoke(
        file: File
    ) = repository.uploadDocument(file)
}