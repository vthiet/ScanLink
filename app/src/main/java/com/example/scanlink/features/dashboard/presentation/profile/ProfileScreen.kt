package com.example.scanlink.features.dashboard.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanlink.features.dashboard.presentation.preferences.DashboardPreferencesViewModel

@Composable
fun ProfileScreen(
    preferencesViewModel: DashboardPreferencesViewModel = hiltViewModel()
) {
    val preferencesState by preferencesViewModel.state.collectAsStateWithLifecycle()

    ProfileContent(
        preferencesState = preferencesState,
        onPreferencesEvent = preferencesViewModel::onEvent
    )
}
