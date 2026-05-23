package com.example.scanlink.features.authentication.domain.usecases

import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String? = null,
        dateOfBirth: String? = null,
        gender: String? = null
    ): Result<UserEntity> {
        return try {
            authRepository.register(email, password, displayName, dateOfBirth, gender)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
