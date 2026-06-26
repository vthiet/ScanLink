package com.example.scanlink.features.document_scanner.domain.usecases

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.entities.CropRect
import com.example.scanlink.features.document_scanner.domain.repositories.IPreviewImageRepository
import javax.inject.Inject

class CropPreviewImageUseCase @Inject constructor(
    private val previewImageRepository: IPreviewImageRepository
) {
    operator fun invoke(bitmap: Bitmap, cropRect: CropRect): Bitmap {
        return previewImageRepository.cropByRect(bitmap, cropRect)
    }
}
