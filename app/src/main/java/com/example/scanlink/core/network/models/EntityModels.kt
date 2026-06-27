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
    val id: String,
    val ownerUid: String,
    val title: String,
    val storageUrl: String,
    val fileSize: Long,
    val extractedText: String?,
    val createdAt: String,
    val updatedAt: String
)