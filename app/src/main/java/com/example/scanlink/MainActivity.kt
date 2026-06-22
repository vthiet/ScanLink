package com.example.scanlink
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scanlink.core.ui.theme.ScanLinkTheme
import com.example.scanlink.features.authentication.presentation.register.RegisterViewModel
import com.example.scanlink.navigation.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import org.opencv.android.OpenCVLoader

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (OpenCVLoader.initLocal()) {
            Log.d("OpenCV", "OpenCV loaded successfully")
        } else {
            Log.e("OpenCV", "OpenCV initialization failed")
        }

        setContent {
             ScanLinkTheme {
                 val viewModel: RegisterViewModel = hiltViewModel()

                 AppNavigation(
                     registerViewModel = viewModel
                 )
             }
        }
    }
}
