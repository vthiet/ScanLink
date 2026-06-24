package com.example.scanlink.features.authentication.presentation.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.features.authentication.presentation.component.AuthErrorBanner
import com.example.scanlink.features.authentication.presentation.component.AuthPasswordTextField
import com.example.scanlink.features.authentication.presentation.component.AuthTextField
import com.example.scanlink.features.authentication.presentation.component.DatePickerField
import com.example.scanlink.features.authentication.presentation.component.GenderDropdownField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    viewModel: RegisterViewModel,
    state: RegisterState,
    onNavigateToLogin: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create new account", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Text(text = "Please fill out information below", fontSize = 14.sp, color = Color.Gray)

            // ── Field: Display Name ────────────────────────────────────────────
            AuthTextField(
                value = state.displayNameInput,
                label = "Display name",
                icon = Icons.Default.Person,
                errorResId = state.displayNameErrorResId,
                onValueChange = { viewModel.onEvent(RegisterEvent.DisplayNameChanged(it)) }
            )

            // ── Field: Email (validation cục bộ + email trùng từ Firebase/backend) ─
            AuthTextField(
                value = state.emailInput,
                label = "Email",
                icon = Icons.Default.Email,
                errorResId = state.emailErrorResId,
                onValueChange = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) }
            )

            // ── Field: Password (validation cục bộ) ───────────────────────────
            AuthPasswordTextField(
                value = state.passwordInput,
                label = "Password",
                icon = Icons.Default.Password,
                errorResId = state.passwordErrorResId,
                onValueChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) }
            )

            // ── Field: Confirm Password ────────────────────────────────────────
            AuthPasswordTextField(
                value = state.confirmPasswordInput,
                label = "Confirm password",
                icon = Icons.Default.Password,
                errorResId = state.confirmPasswordErrorResId,
                onValueChange = { viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            } else {
                Button(
                    onClick = { viewModel.onEvent(RegisterEvent.Submit) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) { Text("Register", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }

            // ── Error Banner: lỗi server / network ────────────────────────────
            // Hiển thị khi backend không phản hồi (network timeout, no internet),
            // lỗi 500, token hết hạn, bad request (400)...
            // KHÔNG hiển thị lỗi email trùng ở đây — lỗi đó hiển thị dưới field Email bên trên.
            AuthErrorBanner(errorResId = state.errorResId)

            TextButton(onClick = onNavigateToLogin) {
                Text("You have account? Login here")
            }
        }
    }
}
