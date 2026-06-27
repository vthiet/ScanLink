package com.example.scanlink.features.document_scanner.domain.usecases

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.repositories.IScanProcessingRepository
import java.io.File
import javax.inject.Inject

class CreateMultiImagePdfUseCase @Inject constructor(
    private val scanProcessingRepository: IScanProcessingRepository
) {
    /**
     * Thực hiện chọn nhiều ảnh, xử lý và gộp thành một file PDF duy nhất.
     * 
     * @param bitmaps Danh sách các ảnh Bitmap đầu vào.
     * @param fileName Tên file PDF muốn lưu (không cần đuôi .pdf).
     * @return File PDF sau khi đã tạo, hoặc null nếu có lỗi.
     */
    suspend operator fun invoke(bitmaps: List<Bitmap>, fileName: String): File? {
        return scanProcessingRepository.processMultipleImages(bitmaps, fileName)
    }
}
