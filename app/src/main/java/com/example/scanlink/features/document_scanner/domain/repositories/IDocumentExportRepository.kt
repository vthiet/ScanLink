package com.example.scanlink.features.document_scanner.domain.repositories

import android.content.Context
import java.io.File

interface IDocumentExportRepository {
    fun createPdfFromImageUris(
        context: Context,
        imageUris: List<String>,
        fileName: String
    ): File
}
