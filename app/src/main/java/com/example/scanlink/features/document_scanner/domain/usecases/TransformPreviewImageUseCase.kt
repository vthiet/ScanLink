package com.example.scanlink.features.document_scanner.domain.usecases

import com.example.scanlink.features.document_scanner.domain.repositories.IPreviewImageRepository
import javax.inject.Inject

/**
 * UseCase thực hiện các biến đổi cơ bản (xoay, lật) trên ảnh xem trước.
 */
class TransformPreviewImageUseCase @Inject constructor(
    private val previewImageRepository: IPreviewImageRepository
) {
    operator fun invoke(
        image: Any,
        rotation: Float,
        flipHorizontal: Boolean,
        flipVertical: Boolean,
        cropCenter: Boolean
    ): Any {
        return previewImageRepository.transform(
            image = image,
            rotation = rotation,
            flipHorizontal = flipHorizontal,
            flipVertical = flipVertical,
            cropCenter = cropCenter
        )
    }
}
