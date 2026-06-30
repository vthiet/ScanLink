package com.example.scanlink.features.document_scanner.domain.repositories

import com.example.scanlink.features.document_scanner.domain.entities.CropRect

interface IPreviewImageRepository {
    suspend fun loadImage(uriString: String): Any
    fun transform(image: Any, rotation: Float, flipHorizontal: Boolean, flipVertical: Boolean, cropCenter: Boolean): Any
    suspend fun saveImage(image: Any, fileName: String): String
    fun cropImage(image: Any, cropRect: CropRect): Any
}
