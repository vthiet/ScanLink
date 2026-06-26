package com.example.scanlink.features.document_scanner.domain.repositories

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.scanlink.features.document_scanner.domain.entities.CropRect

interface IPreviewImageRepository {
    fun loadBitmap(context: Context, uri: Uri): Bitmap

    fun transform(
        bitmap: Bitmap,
        rotation: Float,
        flipHorizontal: Boolean,
        flipVertical: Boolean,
        cropCenter: Boolean
    ): Bitmap

    fun saveToPictures(context: Context, bitmap: Bitmap, fileName: String): Uri

    fun cropByRect(bitmap: Bitmap, cropRect: CropRect): Bitmap
}
