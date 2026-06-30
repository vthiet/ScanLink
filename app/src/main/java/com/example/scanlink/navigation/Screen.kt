package com.example.scanlink.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Transfer : Screen("transfer") {
        const val tabRoute = "transfer/{tab}/{documentId}"

        fun createRoute(tab: String, documentId: String? = null): String {
            return "transfer/${Uri.encode(tab)}/${Uri.encode(documentId ?: "none")}"
        }
    }
    object History : Screen("history")
    object Profile : Screen("profile")
    object Camera : Screen("camera")
    object OcrResult : Screen("ocr")
    object BatchPreview : Screen("batch_preview/{encodedUris}") {
        fun createRoute(encodedUris: String = "session"): String {
            return "batch_preview/${Uri.encode(encodedUris)}"
        }
    }

    object FileDetail : Screen("file_detail/{documentId}") {
        fun createRoute(documentId: String): String {
            return "file_detail/${Uri.encode(documentId)}"
        }
    }

    object Preview : Screen("preview/{uri}") {
        fun createRoute(uri: String): String {
            return "preview/${Uri.encode(uri)}"
        }
    }
}
