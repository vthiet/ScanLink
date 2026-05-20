package com.example.scanlink.features.authentication.domain.entities

data class UserEntity (
    // Firebase Authentication fields
    val uid: String,
    val email: String?,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isVerifyEmail: Boolean = false,

    // Custom fields
    val dateOfBirth: String? = null,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)