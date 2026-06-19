package com.example.scanlink.features.file_sharing.presentation.scan

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CameraScreen(
    onClose: () -> Unit,
    onNavigateToPreview: (String) -> Unit
) {
    val context = LocalContext.current
    val viewModel: CameraViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()

    var hasCameraPermission by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // Kiểm tra và yêu cầu quyền
    LaunchedEffect(Unit) {
        hasCameraPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    when {
        !hasCameraPermission -> {
            PermissionDeniedScreen(onRequestPermission = {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            })
        }

        else -> {
            CameraContent(
                onClose = onClose,
                onPhotoCaptured = { uri ->
                    onNavigateToPreview(uri)
                },
                viewModel = viewModel
            )
        }
    }
}

@Composable
private fun PermissionDeniedScreen(onRequestPermission: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0C)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Cần quyền Camera để quét tài liệu",
            color = Color.White
        )
    }
}