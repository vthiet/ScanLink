package com.example.scanlink.features.authentication.domain.usecases

import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.repositories.IAuthenticationRepository
import javax.inject.Inject

class UpdateDisplayNameUseCase @Inject constructor(
    private val authRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(displayName: String): Result<UserEntity> {
        return authRepository.updateDisplayName(displayName)
    }
}
