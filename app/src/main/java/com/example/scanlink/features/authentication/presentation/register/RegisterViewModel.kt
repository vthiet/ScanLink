package com.example.scanlink.features.authentication.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.R
import com.example.scanlink.core.toUserFriendlyErrorResId
import com.example.scanlink.features.authentication.domain.usecases.RegisterWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterWithEmailUseCase
) : ViewModel() {

    // private, just viewmodel can change
    private val _state: MutableStateFlow<RegisterState> = MutableStateFlow(RegisterState())

    // public for compose read
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EmailChanged -> onEventEmailChanged(event)

            is RegisterEvent.PasswordChanged -> onEventPasswordChanged(event)

            is RegisterEvent.ConfirmPasswordChanged -> onEventConfirmPasswordChanged(event)

            is RegisterEvent.DisplayNameChanged -> onEventDisplayNameChanged(event)

            is RegisterEvent.DateOfBirthChanged -> onEventDateOfBirthChanged(event)

            is RegisterEvent.GenderChanged -> onEventGenderChanged(event)

            RegisterEvent.Submit -> performRegistration()
        }
    }

    private fun onEventConfirmPasswordChanged(event: RegisterEvent.ConfirmPasswordChanged) {
        val input = event.value

        val currentPassword = _state.value.passwordInput

        val error: Int? = if (input != currentPassword) {
            R.string.error_password_mismatch
        } else {
            null
        }

        _state.update { currentState ->
            currentState.copy(
                confirmPasswordInput = input,
                confirmPasswordErrorResId = error
            )
        }
    }

    private fun onEventEmailChanged(event: RegisterEvent.EmailChanged) {
        val input = event.value

        val error: Int? = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
            R.string.error_email_invalid
        } else {
            null
        }

        _state.update { currentState ->
            currentState.copy(
                emailInput = input,
                emailErrorResId = error
            )
        }
    }

    private fun onEventPasswordChanged(event: RegisterEvent.PasswordChanged) {
        val input = event.value

        val error: Int? = if (input.length < 6) {
            R.string.error_password_short
        } else {
            null
        }

        _state.update { currentState ->
            var newState = currentState.copy(
                passwordInput = input,
                passwordErrorResId = error
            )

            if (newState.confirmPasswordInput.isNotEmpty()) {
                val confirmError = if (input != newState.confirmPasswordInput) {
                    R.string.error_password_mismatch
                } else null

                newState = newState.copy(confirmPasswordErrorResId = confirmError)
            }

            newState
        }
    }

    private fun onEventDisplayNameChanged(event: RegisterEvent.DisplayNameChanged) {
        val input = event.value

        val error: Int? = if (input.isEmpty()) {
            R.string.error_display_name_empty
        } else {
            null
        }

        _state.update {
            it.copy(
                displayNameInput = input,
                displayNameErrorResId = error
            )
        }
    }

    private fun onEventDateOfBirthChanged(event: RegisterEvent.DateOfBirthChanged) {
        val input = event.value
        var errorResId: Int? = null

        if (input.isEmpty()) {
            errorResId = R.string.error_dob_empty
        } else {
            try {
                val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dob = LocalDate.parse(input, formatter)
                val today = LocalDate.now()

                if (dob.isAfter(today)) {
                    errorResId = R.string.error_dob_future
                }
            } catch (e: DateTimeParseException) {
                errorResId = R.string.error_dob_invalid
            }
        }

        _state.update { currentState ->
            currentState.copy(
                dateOfBirthInput = input,
                dateOfBirthErrorResId = errorResId
            )
        }
    }

    private fun onEventGenderChanged(event: RegisterEvent.GenderChanged) {
        _state.update { it.copy(genderInput = event.value) }
    }

    private fun performRegistration() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorResId = null) }

            val result = registerUseCase(
                email = _state.value.emailInput,
                password = _state.value.passwordInput,
                displayName = _state.value.displayNameInput,
                dateOfBirth = _state.value.dateOfBirthInput,
                gender = _state.value.genderInput
            )

            result.fold(
                onSuccess = { entity ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            successUser = entity
                        )
                    }
                },
                onFailure = { err ->
                    err.printStackTrace()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            emailErrorResId = err.toUserFriendlyErrorResId()
                        )
                    }
                }
            )
        }
    }
}
