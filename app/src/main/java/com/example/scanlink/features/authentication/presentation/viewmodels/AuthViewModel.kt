package com.example.scanlink.features.authentication.presentation.viewmodels

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.authentication.domain.entities.UserEntity
import com.example.scanlink.features.authentication.domain.usecases.GetCurrentUserUseCase
import com.example.scanlink.features.authentication.domain.usecases.LoginUseCase
import com.example.scanlink.features.authentication.domain.usecases.LogoutUseCase
import com.example.scanlink.features.authentication.domain.usecases.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    data object Uninitialized : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val userEntity: UserEntity) : AuthState()
    data object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

data class AuthUiState(
    val authState: AuthState = AuthState.Uninitialized,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkCurrentUser()
    }

    fun checkCurrentUser() {
        viewModelScope.launch {
            val currentUser = getCurrentUserUseCase()
            if (currentUser != null) {
                _uiState.update { it.copy(authState = AuthState.Authenticated(currentUser)) }
            } else {
                _uiState.update { it.copy(authState = AuthState.Unauthenticated) }
            }
        }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, passwordError = null) }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, confirmPasswordError = null) }
    }

    fun register() {
        val currentState = _uiState.value
        val email = currentState.email.trim()
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        // Validation
        var hasError = false
        val emailError = if (email.isEmpty()) {
            hasError = true
            "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            hasError = true
            "Invalid email format"
        } else {
            null
        }

        val passwordError = if (password.isEmpty()) {
            hasError = true
            "Password is required"
        } else if (password.length < 6) {
            hasError = true
            "Password must be at least 6 characters"
        } else {
            null
        }

        val confirmPasswordError = if (confirmPassword.isEmpty()) {
            hasError = true
            "Please confirm password"
        } else if (password != confirmPassword) {
            hasError = true
            "Passwords do not match"
        } else {
            null
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(authState = AuthState.Loading) }
            registerUseCase(email, password).fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(authState = AuthState.Authenticated(user)) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(authState = AuthState.Error(error.message ?: "Registration failed"))
                    }
                }
            )
        }
    }

    fun login() {
        val currentState = _uiState.value
        val email = currentState.email.trim()
        val password = currentState.password

        // Validation
        var hasError = false
        val emailError = if (email.isEmpty()) {
            hasError = true
            "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            hasError = true
            "Invalid email format"
        } else {
            null
        }

        val passwordError = if (password.isEmpty()) {
            hasError = true
            "Password is required"
        } else {
            null
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(authState = AuthState.Loading) }
            loginUseCase(email, password).fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(authState = AuthState.Authenticated(user)) }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(authState = AuthState.Error(error.message ?: "Login failed"))
                    }
                }
            )
        }
    }

    fun logout() {
        logoutUseCase()
        _uiState.update {
            it.copy(
                authState = AuthState.Unauthenticated,
                email = "",
                password = "",
                confirmPassword = ""
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(authState = AuthState.Unauthenticated) }
    }
}

