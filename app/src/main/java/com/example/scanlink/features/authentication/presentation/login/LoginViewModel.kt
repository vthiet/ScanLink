package com.example.scanlink.features.authentication.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.R
import com.example.scanlink.core.toUserFriendlyErrorResId
import com.example.scanlink.features.authentication.domain.usecases.LoginWithEmailUseCase
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginWithEmailUseCase
) : ViewModel() {

    private val _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> onEventEmailChanged(event)
            is LoginEvent.PasswordChanged -> onEventPasswordChanged(event)
            LoginEvent.Submit -> performLogin()
        }
    }

    private fun onEventEmailChanged(event: LoginEvent.EmailChanged) {
        val input = event.value
        val error: Int? = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            R.string.error_email_invalid
        } else {
            null
        }
        _state.update { it.copy(emailInput = input, emailErrorResId = error) }
    }

    private fun onEventPasswordChanged(event: LoginEvent.PasswordChanged) {
        val input = event.value
        val error: Int? = if (input.length < 6) {
            R.string.error_password_short
        } else {
            null
        }
        _state.update { it.copy(passwordInput = input, passwordErrorResId = error) }
    }

    private fun performLogin() {
        viewModelScope.launch {
            // Xóa tất cả lỗi cũ trước khi thử đăng nhập mới
            _state.update {
                it.copy(
                    isLoading = true,
                    errorResId = null,
                    passwordErrorResId = null
                )
            }

            val result = loginUseCase(
                email = _state.value.emailInput,
                password = _state.value.passwordInput
            )

            result.fold(
                onSuccess = { entity ->
                    _state.update { it.copy(isLoading = false, successUser = entity) }
                },
                onFailure = { err ->
                    err.printStackTrace()
                    when (err) {
                        // Sai email/password Firebase → hiển thị lỗi ngay bên dưới field Password
                        is FirebaseAuthInvalidCredentialsException ->
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    passwordErrorResId = R.string.error_wrong_password
                                )
                            }

                        // Tất cả lỗi còn lại (404 account not synced, network, 500, token...)
                        // → hiển thị tại error banner phía dưới nút Login
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
}
