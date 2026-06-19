package com.example.scanlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scanlink.features.authentication.presentation.RegisterContent
import com.example.scanlink.features.authentication.presentation.RegisterState
import com.example.scanlink.features.authentication.presentation.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            val viewModel: RegisterViewModel = viewModel()

            val state = RegisterState(
                displayNameInput = "",
                emailInput = "",
                passwordInput = "",
                dateOfBirthInput = "",
                genderInput = "",
                isLoading = false
            )

            RegisterContent(
                viewModel = viewModel,
                state = state,
                onNavigateToLogin = {}
            )
        }
    }
}