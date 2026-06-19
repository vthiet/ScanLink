package com.example.scanlink.features.authentication.data.repositories

import com.example.scanlink.features.authentication.data.datasources.FirebaseAuthDataSource
import com.example.scanlink.features.authentication.data.models.toEntity
import com.example.scanlink.features.authentication.data.models.toUserProfile
import com.example.scanlink.features.authentication.data.remote.AuthRemoteDataSource
import com.example.scanlink.features.authentication.data.remote.BackendApiService
import com.example.scanlink.features.authentication.data.remote.BackendRegisterRequest
import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.AuthRepository
import com.example.scanlink.features.authentication.domain.repositories.IAuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val apiService: BackendApiService
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
t
            val tokenResult = firebaseUser.getIdToken(true).await()
            val idToken = tokenResult.token ?: throw Exception("Không thể lấy Token bảo mật")

            val backendRequest = BackendRegisterRequest(displayName, dateOfBirth, gender)
            val backendData = apiService.syncUserWithBackend("Bearer $idToken", backendRequest)

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
                    role = backendData.role,
                    isActive = backendData.active,
                    createdAt = backendData.createdAt,
                    updatedAt = backendData.updatedAt
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}
