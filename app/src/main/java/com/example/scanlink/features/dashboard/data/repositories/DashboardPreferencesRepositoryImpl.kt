package com.example.scanlink.features.dashboard.data.repositories

import android.content.Context
import android.content.res.Configuration
import com.example.scanlink.features.dashboard.domain.entities.DashboardPreferences
import com.example.scanlink.features.dashboard.domain.repositories.IDashboardPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context
) : IDashboardPreferencesRepository {

    private val sharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val defaultDarkTheme =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES

    private val _preferences = MutableStateFlow(
        DashboardPreferences(
            isDarkTheme = sharedPreferences.getBoolean(KEY_DARK_THEME, defaultDarkTheme)
        )
    )

    override val preferences: Flow<DashboardPreferences> = _preferences.asStateFlow()

    override suspend fun setDarkTheme(isDarkTheme: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_DARK_THEME, isDarkTheme)
            .apply()

        _preferences.value = _preferences.value.copy(isDarkTheme = isDarkTheme)
    }

    private companion object {
        const val PREFERENCES_NAME = "scanlink_dashboard_preferences"
        const val KEY_DARK_THEME = "dark_theme"
    }
}
