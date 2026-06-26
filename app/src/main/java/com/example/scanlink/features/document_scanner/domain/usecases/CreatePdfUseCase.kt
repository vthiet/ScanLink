package com.example.scanlink.features.document_scanner.domain.usecases

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.repositories.IScanProcessingRepository
import java.io.File
import javax.inject.Inject

class CreatePdfUseCase @Inject constructor(
    private val scanProcessingRepository: IScanProcessingRepository
) {
    operator fun invoke(bitmap: Bitmap, fileName: String): File? {
        return scanProcessingRepository.createPdf(bitmap, fileName)
    }
}
