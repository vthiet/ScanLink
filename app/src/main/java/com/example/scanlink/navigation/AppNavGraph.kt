package com.example.scanlink.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.scanlink.features.dashboard.presentation.home.HomeScreen
import com.example.scanlink.features.dashboard.presentation.profile.ProfileScreen
import com.example.scanlink.features.document_scanner.presentation.camera.CameraScreen
import com.example.scanlink.features.document_scanner.presentation.camera.CameraViewModel
import com.example.scanlink.features.document_scanner.presentation.ocr.OcrResultScreen
import com.example.scanlink.features.document_scanner.presentation.preview.PreviewScreen
import com.example.scanlink.features.document_scanner.presentation.transfer.TransferScreen
import com.example.scanlink.features.file_sharing.presentation.history.HistoryScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {

        composable(Screen.Home.route) {
            HomeScreen()
        }

        composable(Screen.Transfer.route) {
            TransferScreen()
        }

        composable(Screen.History.route) {
            HistoryScreen()
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        composable(Screen.Camera.route) { backStackEntry ->
            val cameraViewModel: CameraViewModel = hiltViewModel(backStackEntry)

            CameraScreen(
                viewModel = cameraViewModel,
                onClose = {
                    navController.popBackStack()
                },
                onNavigateToPreview = { uri ->
                    navController.navigate(Screen.Preview.createRoute(uri))
                },
                onNavigateToOcrResult = {
                    navController.navigate(Screen.OcrResult.route)
                }
            )
        }

        composable(Screen.OcrResult.route) { currentBackStackEntry ->
            val cameraBackStackEntry = remember(currentBackStackEntry) {
                navController.getBackStackEntry(Screen.Camera.route)
            }

            val sharedCameraViewModel: CameraViewModel = hiltViewModel(cameraBackStackEntry)

            OcrResultScreen(
                viewModel = sharedCameraViewModel,
                onBackClick = {
                    navController.popBackStack(Screen.Home.route, false)
                }
            )
        }

        composable(Screen.Preview.route) { backStackEntry ->

            val imageUri = Uri.decode(
                backStackEntry.arguments?.getString("uri") ?: ""
            )

            PreviewScreen(
                imageUri = imageUri,
                onClose = {
                    navController.popBackStack()
                },
                onSave = {
                    navController.navigate(Screen.OcrResult.route)
                },
                onRetake = {
                    navController.popBackStack()
                }
            )
        }
    }
}