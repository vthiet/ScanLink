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
    val displayName: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val displayNameError: String? = null,
    val dateOfBirthError: String? = null,
    val genderError: String? = null
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

    fun updateDisplayName(displayName: String) {
        _uiState.update { it.copy(displayName = displayName, displayNameError = null) }
    }

    fun updateDateOfBirth(dateOfBirth: String) {
        _uiState.update { it.copy(dateOfBirth = dateOfBirth, dateOfBirthError = null) }
    }

    fun updateGender(gender: String) {
        _uiState.update { it.copy(gender = gender, genderError = null) }
    }

    fun register(
        email: String,
        password: String,
        displayName: String,
        dateOfBirth: String,
        gender: String
    ) {
        val trimmedEmail = email.trim()
        val trimmedDisplayName = displayName.trim()
        val trimmedDateOfBirth = dateOfBirth.trim()
        val trimmedGender = gender.trim()
        val confirmPassword = _uiState.value.confirmPassword

        // Validation
        var hasError = false
        val emailError = if (trimmedEmail.isEmpty()) {
            hasError = true
            "Email is required"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
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

        val displayNameError = if (trimmedDisplayName.isEmpty()) {
            hasError = true
            "Display name is required"
        } else {
            null
        }

        val dateOfBirthError = if (trimmedDateOfBirth.isEmpty()) {
            hasError = true
            "Date of birth is required"
        } else {
            null
        }

        val genderError = if (trimmedGender.isEmpty()) {
            hasError = true
            "Gender is required"
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
                    confirmPasswordError = confirmPasswordError,
                    displayNameError = displayNameError,
                    dateOfBirthError = dateOfBirthError,
                    genderError = genderError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(authState = AuthState.Loading) }
            registerUseCase(
                email = trimmedEmail,
                password = password,
                displayName = trimmedDisplayName,
                dateOfBirth = trimmedDateOfBirth,
                gender = trimmedGender
            ).fold(
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
                confirmPassword = "",
                displayName = "",
                dateOfBirth = "",
                gender = ""
            )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(authState = AuthState.Unauthenticated) }
    }
}
