package com.example.scanlink.features.authentication.domain.entities

data class UserEntity (
    // Firebase Authentication fields
    val uid: String,
    val email: String?,
    val phoneNumber: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false,
    val providerId: String? = null,

    // Custom fields, Spring Boot
    val dateOfBirth: String? = null,
    val gender: String? = null,
    val role: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long? = null,
    val updatedAt: Long? = null,
)