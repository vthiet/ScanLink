package com.example.scanlink.features.dashboard.domain.usecases

import com.example.scanlink.features.dashboard.domain.repositories.IDashboardPreferencesRepository
import javax.inject.Inject

class SetDarkThemePreferenceUseCase @Inject constructor(
    private val preferencesRepository: IDashboardPreferencesRepository
) {
    suspend operator fun invoke(isDarkTheme: Boolean) {
        preferencesRepository.setDarkTheme(isDarkTheme)
    }
}
