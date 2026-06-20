package com.example.scanlink.features.authentication.data.mapper

import com.example.scanlink.features.authentication.data.datasources.remote.dto.RegisterResponse
import com.example.scanlink.features.authentication.domain.entities.UserEntity

fun RegisterResponse.toUserEntity(): UserEntity {
    return UserEntity(
        uid = uid,
        email = email,
        displayName = displayName,
        dateOfBirth = dateOfBirth,
        gender = gender,
        role = role,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}