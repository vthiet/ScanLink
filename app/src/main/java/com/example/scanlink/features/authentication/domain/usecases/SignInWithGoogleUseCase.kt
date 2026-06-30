package com.example.scanlink.features.authentication.domain.usecases

import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.IAuthenticationRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val repository: IAuthenticationRepository
) {
    suspend operator fun invoke(googleIdToken: String): Result<UserEntity> {
        return try {
            repository.signInWithGoogle(googleIdToken)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
