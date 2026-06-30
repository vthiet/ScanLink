package com.example.scanlink.features.authentication.domain.usecases

import com.example.scanlink.features.authentication.domain.repositories.IAuthenticationRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: IAuthenticationRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.logout()
    }
}
