package com.example.scanlink.features.authentication.data.repositories

import com.example.scanlink.features.authentication.data.datasources.FirebaseAuthDataSource
import com.example.scanlink.features.authentication.data.models.toUserProfile
import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun register(
        email: String,
        password: String
    ): UserEntity {
        val firebaseUser = firebaseAuthDataSource.register(email, password)
        return firebaseUser.toUserProfile()
    }

    override suspend fun login(
        email: String,
        password: String
    ): UserEntity {
        val firebaseUser = firebaseAuthDataSource.login(email, password)
        return firebaseUser.toUserProfile()
    }

    override fun logout() {
        firebaseAuthDataSource.logout()
    }

    override fun getCurrentUser(): UserEntity? {
        return firebaseAuthDataSource.getCurrentUser()?.toUserProfile()
    }
}