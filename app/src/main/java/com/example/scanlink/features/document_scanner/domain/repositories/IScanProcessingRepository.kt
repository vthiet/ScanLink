package com.example.scanlink.features.document_scanner.domain.repositories

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.entities.ScanFilterType
import java.io.File

interface IScanProcessingRepository {
    fun transformDocument(bitmap: Bitmap): Pair<Bitmap, Boolean>

    fun applyFilters(bitmap: Bitmap, filterType: ScanFilterType): Bitmap

    suspend fun extractText(bitmap: Bitmap): String

    fun createPdf(bitmap: Bitmap, fileName: String): File?

    suspend fun processMultipleImages(bitmaps: List<Bitmap>, pdfFileName: String): File?
}
