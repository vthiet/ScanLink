package com.example.scanlink.features.dashboard.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.authentication.domain.usecases.GetCurrentUserUseCase
import com.example.scanlink.features.authentication.domain.usecases.LogoutUseCase
import com.example.scanlink.features.authentication.domain.usecases.UpdateDisplayNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateDisplayNameUseCase: UpdateDisplayNameUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState(isLoading = true))
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        loadAccount()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.RefreshAccount -> loadAccount()
            ProfileEvent.AccountDetailsClicked -> {
                _state.update { it.copy(showAccountDetails = true) }
            }
            ProfileEvent.AccountDetailsDismissed -> {
                _state.update { it.copy(showAccountDetails = false) }
            }
            ProfileEvent.RenameClicked -> {
                val currentName = _state.value.user?.displayName.orEmpty()
                _state.update {
                    it.copy(
                        showRenameDialog = true,
                        displayNameInput = currentName,
                        displayNameError = null,
                        errorMessage = null
                    )
                }
            }
            ProfileEvent.RenameDismissed -> {
                _state.update {
                    it.copy(
                        showRenameDialog = false,
                        displayNameError = null
                    )
                }
            }
            is ProfileEvent.DisplayNameChanged -> {
                _state.update {
                    it.copy(
                        displayNameInput = event.value,
                        displayNameError = null
                    )
                }
            }
            ProfileEvent.RenameConfirmed -> updateDisplayName()
            ProfileEvent.LogoutClicked -> {
                _state.update { it.copy(showLogoutConfirmation = true) }
            }
            ProfileEvent.LogoutDismissed -> {
                _state.update { it.copy(showLogoutConfirmation = false) }
            }
            ProfileEvent.LogoutConfirmed -> logout()
            ProfileEvent.LogoutNavigationHandled -> {
                _state.update { it.copy(logoutCompleted = false) }
            }
        }
    }

    private fun loadAccount() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            getCurrentUserUseCase()
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            user = user,
                            isLoading = false,
                            errorMessage = null,
                            displayNameInput = user.displayName.orEmpty()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Không thể tải thông tin tài khoản"
                        )
                    }
                }
        }
    }

    private fun updateDisplayName() {
        val input = _state.value.displayNameInput.trim()
        if (input.isBlank()) {
            _state.update { it.copy(displayNameError = "Tên hiển thị không được để trống") }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isUpdatingName = true,
                    displayNameError = null,
                    errorMessage = null
                )
            }

            updateDisplayNameUseCase(input)
                .onSuccess { user ->
                    _state.update {
                        it.copy(
                            user = user,
                            isUpdatingName = false,
                            showRenameDialog = false,
                            showAccountDetails = false,
                            displayNameInput = user.displayName.orEmpty()
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isUpdatingName = false,
                            displayNameError = error.message ?: "Không thể đổi tên người dùng"
                        )
                    }
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoggingOut = true,
                    showLogoutConfirmation = false,
                    errorMessage = null
                )
            }

            logoutUseCase()
                .onSuccess {
                    _state.update {
                        it.copy(
                            isLoggingOut = false,
                            logoutCompleted = true
                        )
                    }
                }
                .onFailure { error ->
                    _state.update {
                        it.copy(
                            isLoggingOut = false,
                            errorMessage = error.message ?: "Đăng xuất thất bại"
                        )
                    }
                }
        }
    }
}
