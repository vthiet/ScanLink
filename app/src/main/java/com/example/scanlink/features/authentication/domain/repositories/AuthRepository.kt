package com.example.scanlink.features.authentication.domain.repositories

import com.example.scanlink.features.authentication.domain.entities.UserEntity

interface AuthRepository {

    suspend fun register(
        email: String,
        password: String,
        displayName: String? = null,
        dateOfBirth: String? = null
    ): Result<UserEntity>

    suspend fun login(email: String, password: String): Result<UserEntity>

    fun logout()

    fun getCurrentUser(): UserEntity?
}