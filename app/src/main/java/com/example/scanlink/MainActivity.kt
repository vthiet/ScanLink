package com.example.scanlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.scanlink.core.ui.theme.ScanLinkTheme
import com.example.scanlink.features.authentication.presentation.viewmodels.AuthState
import com.example.scanlink.features.authentication.presentation.viewmodels.AuthViewModel
import com.example.scanlink.navigation.AuthNavHost
import com.example.scanlink.navigation.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ScanLinkTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authUiState by authViewModel.uiState.collectAsState()

    when (authUiState.authState) {
        is AuthState.Authenticated -> {
            MainScreen(authViewModel)
        }
        else -> {
            AuthNavHost(
                authViewModel = authViewModel,
                onAuthSuccess = { /* Auth state will be observed and UI will update */ }
            )
        }
    }
}
