package com.example.scanlink.features.document_scanner.domain.usecases

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.entities.ScanFilterType
import com.example.scanlink.features.document_scanner.domain.repositories.IScanProcessingRepository
import javax.inject.Inject

class ApplyScanFilterUseCase @Inject constructor(
    private val scanProcessingRepository: IScanProcessingRepository
) {
    operator fun invoke(bitmap: Bitmap, filterType: ScanFilterType): Bitmap {
        return scanProcessingRepository.applyFilters(bitmap, filterType)
    }
}
