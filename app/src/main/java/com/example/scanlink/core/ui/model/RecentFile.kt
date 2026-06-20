package com.example.scanlink.core.ui.model

import androidx.compose.ui.graphics.Color

data class RecentFile(

    val id: String,

    val name: String,

    val type: FileType,

    val createdAt: String,

    val sizeLabel: String? = null,

    val statusText: String? = null,

    val statusColor: Color = Color(0xFF00CFA4),

    val uploadProgress: Float = 1f
)

enum class FileType {
    PDF,
    DOCX,
    JPG,
    OTHER
}