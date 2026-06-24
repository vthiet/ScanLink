package com.example.scanlink.features.file_sharing.presentation.model.transfer

import com.example.scanlink.core.ui.model.FileType

data class ActiveLink(
    val id: String,
    val fileName: String,
    val fileType: FileType,
    val token: String,
    val createdAt: String,
    val expiresAt: String?,
    val isPublic: Boolean,
    val hasPassword: Boolean,
    val viewCount: Int,
    val isExpiringSoon: Boolean = false
)