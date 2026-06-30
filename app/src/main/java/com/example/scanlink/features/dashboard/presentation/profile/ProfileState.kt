package com.example.scanlink.features.dashboard.presentation.profile

import com.example.scanlink.features.authentication.domain.entities.UserEntity

data class ProfileState(
    val user: UserEntity? = null,
    val isLoading: Boolean = false,
    val isUpdatingName: Boolean = false,
    val isLoggingOut: Boolean = false,
    val errorMessage: String? = null,
    val showAccountDetails: Boolean = false,
    val showRenameDialog: Boolean = false,
    val showLogoutConfirmation: Boolean = false,
    val logoutCompleted: Boolean = false,
    val displayNameInput: String = "",
    val displayNameError: String? = null
)
