package com.example.scanlink.features.authentication.data.repositories

import com.example.scanlink.features.authentication.data.datasources.FirebaseAuthDataSource
import com.example.scanlink.features.authentication.data.models.toEntity
import com.example.scanlink.features.authentication.data.models.toUserProfile
import com.example.scanlink.features.authentication.data.remote.AuthRemoteDataSource
import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.AuthRepository
import com.example.scanlink.features.authentication.domain.repositories.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val remoteDataSource: AuthRemoteDataSource
) : IAuthRepository {
    override suspend fun registerWithEmail(
        displayName: String,
        dateOfBirth: String,
        gender: String,
        email: String,
        password: String
    ): Result<UserEntity> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Không thể khởi tạo tài khoản Firebase")
            val tokenResult = firebaseUser.getIdToken(true).await()
            val idToken = tokenResult.token ?: throw Exception("Không thể lấy Token bảo mật")

            val backendData = remoteDataSource.registerToSpringBoot(
                idToken = idToken,
                displayName = displayName,
                dateOfBirth = dateOfBirth,
                gender = gender
            )

            Result.success(
                UserEntity(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email,
                    displayName = displayName,
                    photoUrl = firebaseUser.photoUrl?.toString(),
                    isEmailVerified = firebaseUser.isEmailVerified,
                    providerId = firebaseUser.providerId,

                    dateOfBirth = dateOfBirth,
                    gender = gender,
                    role = backendData.role ?: "USER",
                    isActive = backendData.isActive,
                    createdAt = backendData.createdAt,
                    updatedAt = backendData.updatedAt
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loginWithEmail(
        email: String,
        password: String
    ): Result<UserEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun logout(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun isLoggedIn(): Result<Boolean> {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrentUser(): Result<UserEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun getIdToken(): Result<String> {
        TODO("Not yet implemented")
    }


}
