package com.example.scanlink.features.authentication.domain.usecases

import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.IAuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: IAuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String,
        dateOfBirth: String,
        gender: String
    ): Result<UserEntity> {
        return try {
            authRepository.registerWithEmail(displayName, dateOfBirth, gender, email, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
