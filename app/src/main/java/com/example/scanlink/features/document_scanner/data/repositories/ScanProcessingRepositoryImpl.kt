package com.example.scanlink.features.document_scanner.data.repositories

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.data.engine.ScanEngine
import com.example.scanlink.features.document_scanner.domain.entities.ScanFilterType
import com.example.scanlink.features.document_scanner.domain.repositories.IScanProcessingRepository
import java.io.File
import javax.inject.Inject

class ScanProcessingRepositoryImpl @Inject constructor(
    private val scanEngine: ScanEngine
) : IScanProcessingRepository {
    override fun transformDocument(image: Any): Pair<Any, Boolean> {
        val bitmap = image as Bitmap
        return scanEngine.transformDocument(bitmap)
    }

    override fun applyFilters(image: Any, filterType: ScanFilterType): Any {
        val bitmap = image as Bitmap
        return scanEngine.applyFilters(bitmap, filterType)
    }

    override suspend fun extractText(image: Any): String {
        val bitmap = image as Bitmap
        return scanEngine.extractText(bitmap)
    }

    override fun createPdf(image: Any, fileName: String): File? {
        val bitmap = image as Bitmap
        return scanEngine.createPdf(bitmap, fileName)
    }

    override suspend fun processMultipleImages(images: List<Any>, pdfFileName: String): File? {
        val bitmaps = images.filterIsInstance<Bitmap>()
        return scanEngine.processMultipleImages(bitmaps, pdfFileName)
    }
}
