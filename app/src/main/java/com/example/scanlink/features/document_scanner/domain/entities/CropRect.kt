package com.example.scanlink.features.document_scanner.domain.entities

data class CropRect(
    val left: Float = 0.08f,
    val top: Float = 0.08f,
    val right: Float = 0.92f,
    val bottom: Float = 0.92f
)
