package com.example.scanlink.features.document_scanner.domain.usecases

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.repositories.IScanProcessingRepository
import javax.inject.Inject

class ExtractTextFromImageUseCase @Inject constructor(
    private val scanProcessingRepository: IScanProcessingRepository
) {
    suspend operator fun invoke(bitmap: Bitmap): String {
        return scanProcessingRepository.extractText(bitmap)
    }
}
