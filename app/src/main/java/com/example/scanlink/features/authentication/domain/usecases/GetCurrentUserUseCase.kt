package com.example.scanlink.features.authentication.domain.usecases

import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.IAuthenticationRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(): Result<UserEntity> {
        return try {
            return authRepository.getCurrentUser()

        } catch (e: Exception){
            return Result.failure(e)
        }

    }
}

