package com.example.scanlink.features.document_scanner.domain.usecases

import com.example.scanlink.features.document_scanner.domain.repositories.IScanProcessingRepository
import javax.inject.Inject

/**
 * UseCase xử lý căn chỉnh và làm phẳng tài liệu.
 */
class TransformDocumentUseCase @Inject constructor(
    private val scanProcessingRepository: IScanProcessingRepository
) {
    operator fun invoke(image: Any): Pair<Any, Boolean> {
        return scanProcessingRepository.transformDocument(image)
    }
}
