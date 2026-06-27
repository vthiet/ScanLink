package com.example.scanlink.features.document_scanner.data.repositories

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.example.scanlink.features.document_scanner.data.image.PreviewImageProcessor
import com.example.scanlink.features.document_scanner.domain.entities.CropRect
import com.example.scanlink.features.document_scanner.domain.repositories.IPreviewImageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PreviewImageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : IPreviewImageRepository {

    override suspend fun loadImage(uriString: String): Any {
        return PreviewImageProcessor.loadBitmap(context, Uri.parse(uriString))
    }

    override fun transform(
        image: Any,
        rotation: Float,
        flipHorizontal: Boolean,
        flipVertical: Boolean,
        cropCenter: Boolean
    ): Any {
        val bitmap = image as Bitmap
        return PreviewImageProcessor.transform(
            bitmap = bitmap,
            rotation = rotation,
            flipHorizontal = flipHorizontal,
            flipVertical = flipVertical,
            cropCenter = cropCenter
        )
    }

    override suspend fun saveImage(image: Any, fileName: String): String {
        val bitmap = image as Bitmap
        return PreviewImageProcessor.saveToPictures(
            context = context,
            bitmap = bitmap,
            fileName = fileName
        ).toString()
    }

    override fun cropImage(image: Any, cropRect: CropRect): Any {
        val bitmap = image as Bitmap
        return PreviewImageProcessor.cropByRect(bitmap, cropRect)
    }
}
