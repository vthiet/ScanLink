package com.example.scanlink.navigation
// class này chứa điều kiện ẩn bottom bar
object NavigationConfig {

    private val hiddenBottomBarRoutes = setOf(
        Screen.Camera.route,
        Screen.OcrResult.route,
        Screen.BatchPreview.route,
        Screen.FileDetail.route
    )

    fun shouldShowBottomBar(currentRoute: String?): Boolean {
        return currentRoute !in hiddenBottomBarRoutes &&
                currentRoute?.startsWith("preview/") != true &&
                currentRoute?.startsWith("batch_preview/") != true &&
                currentRoute?.startsWith("file_detail/") != true
    }
}
