package com.example.scanlink.features.dashboard.presentation.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.dashboard.domain.usecases.ObserveDashboardPreferencesUseCase
import com.example.scanlink.features.dashboard.domain.usecases.SetDarkThemePreferenceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardPreferencesViewModel @Inject constructor(
    observeDashboardPreferencesUseCase: ObserveDashboardPreferencesUseCase,
    private val setDarkThemePreferenceUseCase: SetDarkThemePreferenceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardPreferencesState())
    val state: StateFlow<DashboardPreferencesState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            observeDashboardPreferencesUseCase().collect { preferences ->
                _state.update {
                    it.copy(isDarkTheme = preferences.isDarkTheme)
                }
            }
        }
    }

    fun onEvent(event: DashboardPreferencesEvent) {
        when (event) {
            is DashboardPreferencesEvent.DarkThemeChanged -> setDarkTheme(event.isDarkTheme)
        }
    }

    private fun setDarkTheme(isDarkTheme: Boolean) {
        viewModelScope.launch {
            setDarkThemePreferenceUseCase(isDarkTheme)
        }
    }
}
