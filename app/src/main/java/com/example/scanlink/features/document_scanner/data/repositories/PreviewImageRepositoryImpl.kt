package com.example.scanlink.features.document_scanner.data.repositories

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.scanlink.features.document_scanner.data.image.PreviewImageProcessor
import com.example.scanlink.features.document_scanner.domain.entities.CropRect
import com.example.scanlink.features.document_scanner.domain.repositories.IPreviewImageRepository
import javax.inject.Inject

class PreviewImageRepositoryImpl @Inject constructor() : IPreviewImageRepository {
    override fun loadBitmap(context: Context, uri: Uri): Bitmap {
        return PreviewImageProcessor.loadBitmap(context, uri)
    }

    override fun transform(
        bitmap: Bitmap,
        rotation: Float,
        flipHorizontal: Boolean,
        flipVertical: Boolean,
        cropCenter: Boolean
    ): Bitmap {
        return PreviewImageProcessor.transform(
            bitmap = bitmap,
            rotation = rotation,
            flipHorizontal = flipHorizontal,
            flipVertical = flipVertical,
            cropCenter = cropCenter
        )
    }

    override fun saveToPictures(context: Context, bitmap: Bitmap, fileName: String): Uri {
        return PreviewImageProcessor.saveToPictures(
            context = context,
            bitmap = bitmap,
            fileName = fileName
        )
    }

    override fun cropByRect(bitmap: Bitmap, cropRect: CropRect): Bitmap {
        return PreviewImageProcessor.cropByRect(bitmap, cropRect)
    }
}
