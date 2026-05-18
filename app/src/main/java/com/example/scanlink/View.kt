package com.example.scanlink

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun UserScreen(viewModel: UserViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column {
        Button(onClick = { viewModel.fetchUser() }) {
            Text("Get user information.")
        }

        when {
            uiState.isLoading -> {
                CircularProgressIndicator()
            }
            uiState.errorMessage != null -> {
                Text("Bug: ${uiState.errorMessage}")
            }
            uiState.user != null -> {
                Text("Hello, ${uiState.user?.name}")
            }
        }
    }
}