package com.example.scanlink.features.dashboard.presentation.profile

sealed interface ProfileEvent {
    data object RefreshAccount : ProfileEvent
    data object AccountDetailsClicked : ProfileEvent
    data object AccountDetailsDismissed : ProfileEvent
    data object RenameClicked : ProfileEvent
    data object RenameDismissed : ProfileEvent
    data class DisplayNameChanged(val value: String) : ProfileEvent
    data object RenameConfirmed : ProfileEvent
    data object LogoutClicked : ProfileEvent
    data object LogoutDismissed : ProfileEvent
    data object LogoutConfirmed : ProfileEvent
    data object LogoutNavigationHandled : ProfileEvent
}
