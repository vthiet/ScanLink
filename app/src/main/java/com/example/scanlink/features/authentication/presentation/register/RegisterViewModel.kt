package com.example.scanlink.features.authentication.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.R
import com.example.scanlink.core.exceptions.EmailAlreadyExistsException
import com.example.scanlink.core.toUserFriendlyErrorResId
import com.example.scanlink.features.authentication.domain.usecases.RegisterWithEmailAndPasswordUseCase
import com.example.scanlink.features.authentication.domain.usecases.SignInWithGoogleUseCase
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RegisterViewModel
@Inject
constructor(
    private val registerUseCase: RegisterWithEmailAndPasswordUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<RegisterState> = MutableStateFlow(RegisterState())

    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EmailChanged -> onEventEmailChanged(event)
            is RegisterEvent.PasswordChanged -> onEventPasswordChanged(event)
            is RegisterEvent.ConfirmPasswordChanged -> onEventConfirmPasswordChanged(event)
            is RegisterEvent.DisplayNameChanged -> onEventDisplayNameChanged(event)
            RegisterEvent.Submit -> performRegistration()
            is RegisterEvent.GoogleSignInResult -> performGoogleLogin(event.idToken)
            is RegisterEvent.GoogleSignInFailed -> _state.update {
                it.copy(isLoading = false, errorResId = event.exception.toUserFriendlyErrorResId())
            }
        }
    }

    private fun onEventConfirmPasswordChanged(event: RegisterEvent.ConfirmPasswordChanged) {
        val input = event.value

        val currentPassword = _state.value.passwordInput

        val error: Int? =
                if (input != currentPassword) {
                    R.string.error_password_mismatch
                } else {
                    null
                }

        _state.update { currentState ->
            currentState.copy(confirmPasswordInput = input, confirmPasswordErrorResId = error)
        }
    }

    private fun onEventEmailChanged(event: RegisterEvent.EmailChanged) {
        val input = event.value

        val error: Int? =
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                    R.string.error_email_invalid
                } else {
                    null
                }

        _state.update { currentState ->
            currentState.copy(emailInput = input, emailErrorResId = error)
        }
    }

    private fun onEventPasswordChanged(event: RegisterEvent.PasswordChanged) {
        val input = event.value

        val error: Int? =
                if (input.length < 6) {
                    R.string.error_password_short
                } else {
                    null
                }

        _state.update { currentState ->
            var newState = currentState.copy(passwordInput = input, passwordErrorResId = error)

            if (newState.confirmPasswordInput.isNotEmpty()) {
                val confirmError =
                        if (input != newState.confirmPasswordInput) {
                            R.string.error_password_mismatch
                        } else null

                newState = newState.copy(confirmPasswordErrorResId = confirmError)
            }

            newState
        }
    }

    private fun onEventDisplayNameChanged(event: RegisterEvent.DisplayNameChanged) {
        val input = event.value

        val error: Int? =
                if (input.isEmpty()) {
                    R.string.error_display_name_empty
                } else {
                    null
                }

        _state.update { it.copy(displayNameInput = input, displayNameErrorResId = error) }
    }

    private fun performRegistration() {
        viewModelScope.launch {
            // Xóa lỗi chung cũ trước khi thử đăng ký mới
            _state.update { it.copy(isLoading = true, errorResId = null) }

            val result =
                    registerUseCase(
                            email = _state.value.emailInput,
                            password = _state.value.passwordInput,
                            displayName = _state.value.displayNameInput
                    )

            result.fold(
                    onSuccess = { entity ->
                        _state.update { it.copy(isLoading = false, successUser = entity) }
                    },
                    onFailure = { err ->
                        err.printStackTrace()
                        when (err) {
                            // Email đã có trên Firebase hoặc backend (409)
                            // → hiển thị lỗi ngay bên dưới field Email
                            is FirebaseAuthUserCollisionException,
                            is EmailAlreadyExistsException ->
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        emailErrorResId = err.toUserFriendlyErrorResId()
                                    )
                                }

                            // Tất cả lỗi còn lại (network, 500, token, BadRequest...)
                            // → hiển thị tại error banner phía dưới nút Register
                            else ->
                                _state.update {
                                    it.copy(
                                        isLoading = false,
                                        errorResId = err.toUserFriendlyErrorResId()
                                    )
                                }
                        }
                    }
            )
        }
    }

    private fun performGoogleLogin(idToken: String) {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    errorResId = null
                )
            }

            val result = signInWithGoogleUseCase(idToken)

            result.fold(
                onSuccess = { entity ->
                    _state.update { it.copy(isLoading = false, successUser = entity) }
                },
                onFailure = { err ->
                    err.printStackTrace()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorResId = err.toUserFriendlyErrorResId()
                        )
                    }
                }
            )
        }
    }
}
