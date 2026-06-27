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
    override fun transformDocument(bitmap: Bitmap): Pair<Bitmap, Boolean> {
        return scanEngine.transformDocument(bitmap)
    }

    override fun applyFilters(bitmap: Bitmap, filterType: ScanFilterType): Bitmap {
        return scanEngine.applyFilters(bitmap, filterType)
    }

    override suspend fun extractText(bitmap: Bitmap): String {
        return scanEngine.extractText(bitmap)
    }

    override fun createPdf(bitmap: Bitmap, fileName: String): File? {
        return scanEngine.createPdf(bitmap, fileName)
    }

    override suspend fun processMultipleImages(bitmaps: List<Bitmap>, pdfFileName: String): File? {
        return scanEngine.processMultipleImages(bitmaps, pdfFileName)
    }
}
