package com.example.scanlink.features.authentication.data.datasources.remote.dto

data class RegisterResponse(
    val uid: String,
    val email: String,
    val displayName: String,
    val dateOfBirth: String,
    val gender: String,
    val role: String,
    val isActive: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)