package com.example.scanlink.features.authentication.domain.usecases

import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.IAuthenticationRepository
import javax.inject.Inject

class LoginWithEmailUseCase @Inject constructor(
    private val authRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<UserEntity> {
        return try {
            authRepository.loginWithEmailAndPassword(email, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
