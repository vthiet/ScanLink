package com.example.scanlink.features.dashboard.domain.usecases

import com.example.scanlink.features.dashboard.domain.entities.DashboardPreferences
import com.example.scanlink.features.dashboard.domain.repositories.IDashboardPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDashboardPreferencesUseCase @Inject constructor(
    private val preferencesRepository: IDashboardPreferencesRepository
) {
    operator fun invoke(): Flow<DashboardPreferences> = preferencesRepository.preferences
}
