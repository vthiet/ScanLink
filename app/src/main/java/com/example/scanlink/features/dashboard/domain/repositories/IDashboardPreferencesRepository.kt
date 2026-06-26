package com.example.scanlink.features.dashboard.domain.repositories

import com.example.scanlink.features.dashboard.domain.entities.DashboardPreferences
import kotlinx.coroutines.flow.Flow

interface IDashboardPreferencesRepository {
    val preferences: Flow<DashboardPreferences>

    suspend fun setDarkTheme(isDarkTheme: Boolean)
}
