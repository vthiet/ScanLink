package com.example.scanlink.features.authentication.presentation.register

sealed interface RegisterEffect {

    data object NavigateLogin

    data object NavigateHome

}
