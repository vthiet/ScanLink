package com.example.scanlink.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Transfer : Screen("transfer")
    object History : Screen("history")
    object Profile : Screen("profile")
    object Camera : Screen("camera")
    object OcrResult : Screen("ocr")

    object Preview : Screen("preview/{uri}") {
        fun createRoute(uri: String): String {
            return "preview/${Uri.encode(uri)}"
        }
    }
}