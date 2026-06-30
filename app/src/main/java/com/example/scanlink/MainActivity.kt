package com.example.scanlink

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.scanlink.core.ui.theme.ScanLinkTheme
import com.example.scanlink.features.dashboard.presentation.preferences.DashboardPreferencesViewModel
import com.example.scanlink.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import org.opencv.android.OpenCVLoader

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val preferencesViewModel: DashboardPreferencesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isOpenCvLoaded = OpenCVLoader.initDebug()

        if (isOpenCvLoaded) {
            Log.d("OpenCV", "OpenCV loaded successfully")
        } else {
            Log.e("OpenCV", "OpenCV load failed")
        }

        setContent {
            val preferencesState = preferencesViewModel.state.collectAsStateWithLifecycle()

            ScanLinkTheme(darkTheme = preferencesState.value.isDarkTheme) {
                AppNavigation()
            }
        }
    }
}

