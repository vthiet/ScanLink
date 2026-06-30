package com.example.scanlink.features.dashboard.presentation.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanlink.features.dashboard.presentation.preferences.DashboardPreferencesViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    preferencesViewModel: DashboardPreferencesViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val preferencesState by preferencesViewModel.state.collectAsStateWithLifecycle()
    val profileState by profileViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(profileState.logoutCompleted) {
        if (profileState.logoutCompleted) {
            profileViewModel.onEvent(ProfileEvent.LogoutNavigationHandled)
            onLogout()
        }
    }

    ProfileContent(
        profileState = profileState,
        preferencesState = preferencesState,
        onProfileEvent = profileViewModel::onEvent,
        onPreferencesEvent = preferencesViewModel::onEvent
    )
}
