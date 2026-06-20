package com.example.scanlink.features.authentication.domain.repositories

import com.example.scanlink.features.authentication.domain.entities.UserEntity

interface IAuthRepository {

    suspend fun registerWithEmail(
        displayName: String,
        dateOfBirth: String,
        gender: String,
        email: String,
        password: String
    ): Result<UserEntity>

    suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<UserEntity>

    suspend fun logout(): Result<Unit>

    suspend fun isLoggedIn(): Result<Boolean>

    suspend fun getCurrentUser(): Result<UserEntity>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    suspend fun getIdToken(): Result<String>

}