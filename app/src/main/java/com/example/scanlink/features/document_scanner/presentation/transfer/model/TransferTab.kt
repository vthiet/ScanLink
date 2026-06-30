package com.example.scanlink.features.document_scanner.presentation.transfer.model

enum class TransferTab(val routeValue: String, val label: String) {
    Upload("upload", "Upload"),
    PublicShare("public", "Public Link"),
    PrivateShare("private", "Private Share");

    companion object {
        fun fromRoute(value: String?): TransferTab {
            return entries.firstOrNull { it.routeValue == value } ?: Upload
        }
    }
}
