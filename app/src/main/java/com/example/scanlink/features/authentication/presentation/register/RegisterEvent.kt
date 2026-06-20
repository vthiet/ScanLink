package com.example.scanlink.features.authentication.presentation.register

sealed class RegisterEvent {

    data class EmailChanged(val value: String) : RegisterEvent()

    data class PasswordChanged(val value: String) : RegisterEvent()

    data class DisplayNameChanged(val value: String) : RegisterEvent()

    data class DateOfBirthChanged(val value: String) : RegisterEvent()

    data class GenderChanged(val value: String) : RegisterEvent()

    object Submit : RegisterEvent()

}
