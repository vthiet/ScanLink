package com.example.scanlink.features.authentication.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.authentication.domain.usecases.RegisterWithEmailUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterWithEmailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state = _state.asStateFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EmailChanged -> _state.update { it.copy(emailInput = event.value) }
            is RegisterEvent.PasswordChanged -> _state.update { it.copy(passwordInput = event.value) }
            is RegisterEvent.DisplayNameChanged -> _state.update { it.copy(displayNameInput = event.value) }
            is RegisterEvent.DateOfBirthChanged -> _state.update { it.copy(dobInput = event.value) }
            is RegisterEvent.GenderChanged -> _state.update { it.copy(genderInput = event.value) }
            RegisterEvent.Submit -> performRegistration()
        }
    }

    private fun performRegistration() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val result = registerUseCase(
                email = _state.value.emailInput,
                password = _state.value.passwordInput,
                displayName = _state.value.displayNameInput,
                dateOfBirth = _state.value.dobInput,
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
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = err.localizedMessage
                        )
                    }
                }
            )
        }
    }
}