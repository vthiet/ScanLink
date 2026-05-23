package com.example.scanlink.features.authentication.data.models

import com.example.scanlink.features.authentication.data.remote.UserDto
import com.example.scanlink.features.authentication.domain.entities.UserEntity

fun UserDto.toEntity(): UserEntity {
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
