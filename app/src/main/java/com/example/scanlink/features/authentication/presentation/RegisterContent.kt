package com.example.scanlink.features.authentication.presentation

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.DatePicker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.core.presentation.components.DatePickerField
import com.example.scanlink.features.authentication.presentation.component.AuthPasswordTextField
import com.example.scanlink.features.authentication.presentation.component.AuthTextField
import com.example.scanlink.features.authentication.presentation.component.GenderDropdownField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    viewModel: RegisterViewModel,
    state: RegisterState,
    onNavigateToLogin: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Create new account",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
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
            Text(
                text = "Please fill out information below",
                fontSize = 14.sp,
                color = Color.Gray
            )

            // Display name field
            AuthTextField(
                value = state.displayNameInput,
                label = "Full name",
                icon = Icons.Default.Person,
                onValueChange = { viewModel.onEvent(RegisterEvent.DisplayNameChanged(it)) }
            )

            // Email field
            AuthTextField(
                value = state.emailInput,
                label = "Email",
                icon = Icons.Default.Email,
                onValueChange = {viewModel.onEvent(RegisterEvent.EmailChanged(it))}
            )

            // Password field
            AuthPasswordTextField(
                value = state.passwordInput,
                label = "Password",
                icon = Icons.Default.Password,
                onValueChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it))}
            )

            // DatePicker field
            DatePickerField(
                value = state.dateOfBirthInput,
                label = "Date of birth",
                onDateSelected = { viewModel.onEvent(RegisterEvent.DateOfBirthChanged(it)) }
            )

            GenderDropdownField(
                value = state.genderInput,
                label = "Gender",
                items = listOf("Male", "Female", "Other"),
                onItemSelected = { viewModel.onEvent(RegisterEvent.GenderChanged(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading){
                CircularProgressIndicator(modifier =  Modifier.size(48.dp))
            } else {
                Button(
                    onClick = { viewModel.onEvent(RegisterEvent.Submit)},
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Register", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            state.error?.let{
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp),
                    fontWeight = FontWeight.Medium
                )
            }

            TextButton(onClick =  onNavigateToLogin) {
                Text("You have account? Login here")
            }
        }
    }
}

fun getDatePickerDialog(viewModel: RegisterViewModel, context: Context): DatePickerDialog {
    val calendar = Calendar.getInstance()

    return DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            viewModel.onEvent(RegisterEvent.DateOfBirthChanged("$dayOfMonth/${month + 1}/$year"))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )
}