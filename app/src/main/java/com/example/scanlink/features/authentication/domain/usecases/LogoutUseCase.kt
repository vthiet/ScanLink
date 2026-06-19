package com.example.scanlink.features.authentication.domain.usecases

import com.example.scanlink.features.authentication.domain.repositories.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke() {
        authRepository.logout()
    }
}

