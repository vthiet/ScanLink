package com.example.scanlink.features.document_scanner.presentation.file_detail.components

import com.example.scanlink.features.document_scanner.domain.entities.Document
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

internal val Document.isPdf: Boolean
    get() = mimeType == "application/pdf" || title.endsWith(".pdf", ignoreCase = true)

internal val Document.safePageCount: Int
    get() = when {
        pageCount > 0 -> pageCount
        pages.isNotEmpty() -> pages.size
        else -> 1
    }

internal val Document.previewPath: String?
    get() = thumbnailPath
        ?: pages.firstOrNull()?.imagePath?.takeIf { it.isNotBlank() }
        ?: pdfPath?.takeIf { !isPdf && it.isNotBlank() }

internal fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
}

internal fun formatFileSize(bytes: Long): String {
    if (bytes <= 0L) return "0 KB"
    val kb = bytes / 1024.0
    if (kb < 1024) return "${kb.roundToInt()} KB"
    val mb = kb / 1024.0
    return String.format(Locale.getDefault(), "%.1f MB", mb)
}
