package com.example.scanlink.features.authentication.data.remote

import retrofit2.http.Body
import retrofit2.http.Header

interface BackendApiService {
    suspend fun syncUserWithBackend(
        @Header("Authorization") bearerToken: String,
        @Body request: BackendRegisterRequest
    ): BackendUserResponse
}

data class BackendRegisterRequest(
    val displayName: String,
    val dateOfBirth: String,
    val gender: String
)

data class BackendUserResponse(
    val role: String,
    val active: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)