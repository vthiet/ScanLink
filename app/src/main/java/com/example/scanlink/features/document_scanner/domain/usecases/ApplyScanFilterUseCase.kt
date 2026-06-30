package com.example.scanlink.features.document_scanner.domain.usecases

import com.example.scanlink.features.document_scanner.domain.entities.ScanFilterType
import com.example.scanlink.features.document_scanner.domain.repositories.IScanProcessingRepository
import javax.inject.Inject

class ApplyScanFilterUseCase @Inject constructor(
    private val scanProcessingRepository: IScanProcessingRepository
) {
    /**
     * Áp dụng bộ lọc màu cho ảnh.
     */
    operator fun invoke(image: Any, filterType: ScanFilterType): Any {
        return scanProcessingRepository.applyFilters(image, filterType)
    }
}
