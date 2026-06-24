package com.example.scanlink.features.authentication.domain.usecases

import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.IAuthenticationRepository
import javax.inject.Inject

class RegisterWithEmailAndPasswordUseCase @Inject constructor(
    private val repository: IAuthenticationRepository
) {

    suspend operator fun invoke(
        displayName: String,
        email: String,
        password: String
    ): Result<UserEntity> {
        return try {
            repository.registerWithEmailAndPassword(displayName, email, password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}