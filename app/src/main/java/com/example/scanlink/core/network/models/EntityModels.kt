package com.example.scanlink.core.network.models

data class UserResponse(
    val uid: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val role: String,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class DocumentResponse(
    val id: String?,
    val ownerUid: String?,
    val title: String?,
    val storageUrl: String?,
    val fileSize: Long?,
    val extractedText: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class PageResponse<T>(
    val content: List<T>,
    val pageable: PageableInfo,
    val totalElements: Int,
    val totalPages: Int,
    val last: Boolean
)

data class PageableInfo(
    val pageNumber: Int,
    val pageSize: Int
)

data class CreatePublicShareRequest(
    val documentId: String,
    val password: String? = null,
    val expireInDays: Int? = null
)

data class ShareLinkResponse(
    val hashToken: String,
    val documentId: String,
    val expiresAt: String?,
    val hasPassword: Boolean,
    val shareUrl: String
)

data class GrantPrivatePermissionRequest(
    val documentId: String,
    val shareToEmail: String,
    val role: String
)

data class PrivatePermissionResponse(
    val documentId: String,
    val collaboratorEmail: String,
    val role: String
)