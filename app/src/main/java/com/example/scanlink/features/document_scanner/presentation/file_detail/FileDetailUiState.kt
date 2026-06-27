package com.example.scanlink.features.document_scanner.presentation.file_detail

import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page

data class FileDetailUiState(
    val isLoading: Boolean = true,
    val document: Document? = null,
    val errorMessage: String? = null,
    val isRenameDialogVisible: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
    val renameValue: String = "",
    val actionMessage: String? = null
) {
    val hasOcr: Boolean
        get() = !document?.extractedText.isNullOrBlank()
}

fun fakeFileDetailDocument(id: String = "preview-file"): Document {
    val now = 1_719_371_600_000L
    return Document(
        id = id,
        ownerUid = "demo-user",
        title = when (id) {
            "1" -> "Hop dong Q2_2026.pdf"
            "2" -> "Bien ban hop thang 5.pdf"
            "3" -> "CCCD_MatTruoc.jpg"
            else -> "Document.pdf"
        },
        storageUrl = null,
        fileSize = 2_600_000L,
        extractedText = """
            ScanLink project agreement for mobile document sharing.
            Created for sprint review and internal approval.
            The document includes delivery scope, review notes, and sign-off fields.
            Each scanned page was processed with OCR and exported as a searchable PDF.
            Payment terms, effective date, and team responsibilities are listed below.
            The final copy should be stored in cloud after user confirmation.
            This preview intentionally shows only part of the recognized text.
            Open View All to inspect the complete OCR result.
            Additional clauses continue on the next pages.
        """.trimIndent(),
        pdfPath = null,
        createdAt = 1_719_288_000_000L,
        updatedAt = now,
        isSynced = true,
        pageCount = 4,
        mimeType = if (id == "3") "image/jpeg" else "application/pdf",
        thumbnailPath = null,
        lastModified = now,
        pages = List(4) { index ->
            Page(
                id = "page-${index + 1}",
                documentId = id,
                pageNumber = index + 1,
                imagePath = "",
                ocrText = null,
                createdAt = 1_719_288_000_000L
            )
        }
    )
}
