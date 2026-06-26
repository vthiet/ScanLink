package com.example.scanlink.features.authentication.presentation.login

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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    viewModel: LoginViewModel,
    state: LoginState,
    onNavigateToRegister: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Sign in", fontWeight = FontWeight.Bold) }
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
            Text(
                text = "Welcome back! Please sign in to continue",
                fontSize = 14.sp,
                color = Color.Gray
            )

            // ── Field: Email (validation cục bộ: sai định dạng) ──────────────
            AuthTextField(
                value = state.emailInput,
                label = "Email",
                icon = Icons.Default.Email,
                errorResId = state.emailErrorResId,
                onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) }
            )

            // ── Field: Password (validation cục bộ + sai credentials Firebase) ─
            AuthPasswordTextField(
                value = state.passwordInput,
                label = "Password",
                icon = Icons.Default.Password,
                errorResId = state.passwordErrorResId,
                onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(48.dp))
            } else {
                Button(
                    onClick = { viewModel.onEvent(LoginEvent.Submit) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) { Text("Sign in", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
            }

            // ── Error Banner: lỗi server / network / account issues ────────────
            // Hiển thị khi backend không phản hồi, token hết hạn, tài khoản
            // chưa được đồng bộ sang hệ thống (404), lỗi 500, v.v.
            AuthErrorBanner(errorResId = state.errorResId)

            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Register here")
            }
        }
    }
}
