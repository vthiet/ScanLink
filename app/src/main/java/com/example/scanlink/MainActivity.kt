package com.example.scanlink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.scanlink.core.ui.theme.ScanLinkTheme
import com.example.scanlink.navigation.MainScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScanLinkTheme {
                MainScreen()
            }
        }

    }
}




@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    ScanLinkTheme {
        MainScreen()
    }
}