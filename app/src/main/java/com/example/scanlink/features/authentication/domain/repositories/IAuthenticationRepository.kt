package com.example.scanlink.features.authentication.domain.repositories

import com.example.scanlink.features.authentication.domain.entities.UserEntity

interface IAuthenticationRepository {

    suspend fun registerWithEmailAndPassword(
        displayName: String,
        email: String,
        password: String
    ): Result<UserEntity>

    suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Result<UserEntity>

    suspend fun logout(): Result<Unit>

    suspend fun isLoggedIn(): Result<Boolean>

    suspend fun getCurrentUser(): Result<UserEntity>

    suspend fun updateDisplayName(displayName: String): Result<UserEntity>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    suspend fun getIdToken(): Result<String>

    suspend fun signInWithGoogle(googleIdToken: String): Result<UserEntity>

}
