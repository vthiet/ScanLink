package com.example.scanlink.features.dashboard.presentation.preferences

sealed interface DashboardPreferencesEvent {
    data class DarkThemeChanged(val isDarkTheme: Boolean) : DashboardPreferencesEvent
}
