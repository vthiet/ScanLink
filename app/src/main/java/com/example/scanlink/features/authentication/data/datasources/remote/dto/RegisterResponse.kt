package com.example.scanlink.features.authentication.data.datasources.remote.dto

import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.google.gson.annotations.SerializedName

data class ApiResponse<T> (
    val status: String,
    val message: String,
    val data: T?
)

data class RegisterResponse (
    val uid: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String?,
    val role: String,
    // Jackson strips "is" prefix khi serialize boolean: isActive → active
    @SerializedName("active")
    val isActive: Boolean,
    val dateOfBirth: String?,
    val createdAt: Long?,
    val updatedAt: Long?
)

fun RegisterResponse.toUserEntity(): UserEntity {
    return UserEntity(
        uid = uid,
        email = email,
        displayName = displayName,
        photoUrl = photoUrl,
        dateOfBirth = dateOfBirth,
        role = role,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
