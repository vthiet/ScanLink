package com.example.scanlink.features.document_scanner.domain.repositories

import java.io.File
import com.example.scanlink.features.document_scanner.domain.entities.ScanFilterType

/**
 * Interface nghiệp vụ xử lý ảnh.
 */
interface IScanProcessingRepository {
    /**
     * Nhận diện và làm phẳng tài liệu.
     */
    fun transformDocument(image: Any): Pair<Any, Boolean>

    /**
     * Áp dụng bộ lọc màu.
     */
    fun applyFilters(image: Any, filterType: ScanFilterType): Any

    /**
     * Trích xuất văn bản từ ảnh.
     */
    suspend fun extractText(image: Any): String

    /**
     * Tạo file PDF từ một ảnh.
     */
    fun createPdf(image: Any, fileName: String): File?

    /**
     * Xử lý và tạo PDF từ danh sách ảnh.
     */
    suspend fun processMultipleImages(images: List<Any>, pdfFileName: String): File?
}
