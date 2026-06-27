package com.example.scanlink.features.document_scanner.domain.usecases

import com.example.scanlink.features.document_scanner.domain.repositories.IScanProcessingRepository
import java.io.File
import javax.inject.Inject

class CreatePdfUseCase @Inject constructor(
    private val scanProcessingRepository: IScanProcessingRepository
) {
    /**
     * Tạo file PDF từ ảnh.
     */
    operator fun invoke(image: Any, fileName: String): File? {
        return scanProcessingRepository.createPdf(image, fileName)
    }
}
