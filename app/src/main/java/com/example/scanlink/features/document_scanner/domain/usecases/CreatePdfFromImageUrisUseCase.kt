package com.example.scanlink.features.document_scanner.domain.usecases

import android.content.Context
import com.example.scanlink.features.document_scanner.domain.repositories.IDocumentExportRepository
import java.io.File
import javax.inject.Inject

class CreatePdfFromImageUrisUseCase @Inject constructor(
    private val documentExportRepository: IDocumentExportRepository
) {
    operator fun invoke(
        context: Context,
        imageUris: List<String>,
        fileName: String
    ): File {
        return documentExportRepository.createPdfFromImageUris(
            context = context,
            imageUris = imageUris,
            fileName = fileName
        )
    }
}
