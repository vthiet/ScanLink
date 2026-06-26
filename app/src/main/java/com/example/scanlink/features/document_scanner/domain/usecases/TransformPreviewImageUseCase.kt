package com.example.scanlink.features.document_scanner.domain.usecases

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.repositories.IPreviewImageRepository
import javax.inject.Inject

class TransformPreviewImageUseCase @Inject constructor(
    private val previewImageRepository: IPreviewImageRepository
) {
    operator fun invoke(
        bitmap: Bitmap,
        rotation: Float,
        flipHorizontal: Boolean,
        flipVertical: Boolean,
        cropCenter: Boolean
    ): Bitmap {
        return previewImageRepository.transform(
            bitmap = bitmap,
            rotation = rotation,
            flipHorizontal = flipHorizontal,
            flipVertical = flipVertical,
            cropCenter = cropCenter
        )
    }
}
