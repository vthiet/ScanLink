package com.example.scanlink.features.document_scanner.domain.usecases

import com.example.scanlink.features.document_scanner.domain.repositories.IPreviewImageRepository
import javax.inject.Inject

class LoadPreviewBitmapUseCase @Inject constructor(
    private val previewImageRepository: IPreviewImageRepository
) {
    /**
     * Tải ảnh từ URI string.
     */
    suspend operator fun invoke(uriString: String): Any {
        return previewImageRepository.loadImage(uriString)
    }
}
