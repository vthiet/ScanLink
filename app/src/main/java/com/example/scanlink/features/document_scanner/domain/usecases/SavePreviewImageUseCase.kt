package com.example.scanlink.features.document_scanner.domain.usecases

import com.example.scanlink.features.document_scanner.domain.repositories.IPreviewImageRepository
import javax.inject.Inject

/**
 * UseCase lưu ảnh vào bộ nhớ máy.
 */
class SavePreviewImageUseCase @Inject constructor(
    private val previewImageRepository: IPreviewImageRepository
) {
    /**
     * Trả về URI chuỗi của ảnh đã lưu.
     */
    suspend operator fun invoke(image: Any, fileName: String): String {
        return previewImageRepository.saveImage(image, fileName)
    }
}
