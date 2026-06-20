package com.example.scanlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.scanlink.core.ui.theme.ScanLinkTheme
import com.example.scanlink.features.authentication.domain.repositories.IAuthRepository
import com.example.scanlink.features.authentication.presentation.register.RegisterViewModel
import com.example.scanlink.navigation.AppNavigation
import com.example.scanlink.navigation.MainGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ScanLinkTheme {
                val viewModel: RegisterViewModel = hiltViewModel()

                AppNavigation(
                    registerViewModel = viewModel
                )
            }
//            ScanLinkTheme {
//                MainGraph(,Modifier)
//            }
        }

    }
}

//@Preview(showBackground = true, showSystemUi = true)
//@Composable
//fun GreetingPreview() {
//    ScanLinkTheme {
//        MainScreen()
//    }
//}