package com.example.scanlink.features.authentication.domain.usecases

import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserEntity> {
        return try {
            Result.success(authRepository.login(email, password))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
