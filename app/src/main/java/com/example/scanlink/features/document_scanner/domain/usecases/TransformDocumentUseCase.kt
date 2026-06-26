package com.example.scanlink.features.document_scanner.domain.usecases

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.repositories.IScanProcessingRepository
import javax.inject.Inject

class TransformDocumentUseCase @Inject constructor(
    private val scanProcessingRepository: IScanProcessingRepository
) {
    operator fun invoke(bitmap: Bitmap): Pair<Bitmap, Boolean> {
        return scanProcessingRepository.transformDocument(bitmap)
    }
}
