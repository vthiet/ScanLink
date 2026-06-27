package com.example.scanlink.features.document_scanner.domain.usecases

import com.example.scanlink.features.document_scanner.domain.repositories.IScanProcessingRepository
import javax.inject.Inject

class ExtractTextFromImageUseCase @Inject constructor(
    private val scanProcessingRepository: IScanProcessingRepository
) {
    /**
     * Trích xuất văn bản từ ảnh.
     */
    suspend operator fun invoke(image: Any): String {
        return scanProcessingRepository.extractText(image)
    }
}
