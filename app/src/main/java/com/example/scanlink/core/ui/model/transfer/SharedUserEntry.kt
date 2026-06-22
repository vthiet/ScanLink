package com.example.scanlink.features.file_sharing.presentation.model.transfer

data class SharedUserEntry(
    val initials: String,
    val email: String,
    val sharedAt: String,
    val role: ShareRole
)

enum class ShareRole { Viewer, Editor }