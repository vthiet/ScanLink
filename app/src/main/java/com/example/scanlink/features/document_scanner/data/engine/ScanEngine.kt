package com.example.scanlink.features.document_scanner.data.engine

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.data.ocr.OCRProcessor
import com.example.scanlink.features.document_scanner.data.opencv.DocumentDetector
import com.example.scanlink.features.document_scanner.data.opencv.ImageFilterProcessor
import com.example.scanlink.features.document_scanner.data.opencv.PerspectiveTransformer
import com.example.scanlink.features.document_scanner.data.pdf.PDFProcessor
import com.example.scanlink.features.document_scanner.domain.entities.ScanFilterType
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanEngine @Inject constructor() {

    private val detector = DocumentDetector()
    private val transformer = PerspectiveTransformer()
    private val filterProcessor = ImageFilterProcessor()
    private val ocrProcessor = OCRProcessor()
    private val pdfProcessor = PDFProcessor()

    /**
     * Bước 1: Nhận diện và cắt góc tài liệu
     */
    fun transformDocument(bitmap: Bitmap): Pair<Bitmap, Boolean> {
        val points = detector.detectDocument(bitmap)
        return if (points != null) {
            transformer.transform(bitmap, points) to true
        } else {
            bitmap to false
        }
    }

    /**
     * Bước 2: Áp dụng bộ lọc dựa trên loại được chọn
     */
    fun applyFilters(bitmap: Bitmap, filterType: ScanFilterType): Bitmap {
        return when (filterType) {
            ScanFilterType.ORIGINAL -> bitmap
            ScanFilterType.B_W -> filterProcessor.applyBlackWhite(bitmap)
            ScanFilterType.GRAYSCALE -> filterProcessor.applyGrayscale(bitmap)
            ScanFilterType.MAGIC_COLOR -> filterProcessor.applyMagicColor(bitmap)
        }
    }

    /**
     * Bước 3: Trích xuất chữ
     */
    suspend fun extractText(bitmap: Bitmap): String {
        val ocrPrep = filterProcessor.applyOcrPreparation(bitmap)
        return ocrProcessor.extractText(ocrPrep)
    }

    /**
     * Bước 4: Tạo PDF
     */
    fun createPdf(bitmap: Bitmap, fileName: String): File? {
        return pdfProcessor.createPdfFromBitmaps(listOf(bitmap), fileName)
    }

    suspend fun fullProcess(bitmap: Bitmap, pdfFileName: String = "ScanLink_Export"): ScanResult {
        val (transformed, detected) = transformDocument(bitmap)
        val filtered = applyFilters(transformed, ScanFilterType.B_W)
        val text = extractText(transformed)
        val pdf = createPdf(filtered, pdfFileName)

        return ScanResult(
            originalBitmap = bitmap,
            processedBitmap = filtered,
            extractedText = text,
            pdfFile = pdf,
            isDocumentDetected = detected
        )
    }

    suspend fun processMultipleImages(
        bitmaps: List<Bitmap>,
        pdfFileName: String = "ScanLink_Batch_Export"
    ): File? {
        if (bitmaps.isEmpty()) return null

        val processedBitmaps = bitmaps.map { bitmap ->
            val (transformed, _) = transformDocument(bitmap)
            applyFilters(transformed, ScanFilterType.B_W)
        }

        return pdfProcessor.createPdfFromBitmaps(processedBitmaps, pdfFileName)
    }

    data class ScanResult(
        val originalBitmap: Bitmap,
        val processedBitmap: Bitmap,
        val extractedText: String,
        val pdfFile: File?,
        val isDocumentDetected: Boolean
    )
}
