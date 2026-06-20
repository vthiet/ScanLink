package com.example.scanlink.features.authentication.data.datasources.remote.dto

data class RegisterRequest(
    val email: String,
    val displayName: String,
    val dateOfBirth: String,
    val gender: String
)
