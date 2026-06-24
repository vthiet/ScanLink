package com.example.scanlink.features.document_scanner.data.engine

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.data.ocr.OCRProcessor
import com.example.scanlink.features.document_scanner.data.opencv.DocumentDetector
import com.example.scanlink.features.document_scanner.data.opencv.ImageFilterProcessor
import com.example.scanlink.features.document_scanner.data.opencv.PerspectiveTransformer
import com.example.scanlink.features.document_scanner.data.pdf.PDFProcessor
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

    data class ScanResult(
        val originalBitmap: Bitmap,
        val processedBitmap: Bitmap,
        val extractedText: String,
        val pdfFile: File?,
        val isDocumentDetected: Boolean
    )

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
     * Bước 2: Áp dụng bộ lọc Đen-Trắng
     */
    fun applyFilters(bitmap: Bitmap): Bitmap {
        return filterProcessor.applyBlackWhite(bitmap)
    }

    /**
     * Bước 3: Trích xuất chữ
     */
    suspend fun extractText(bitmap: Bitmap): String {
        // Tối ưu ảnh cho OCR trước khi đọc
        val ocrPrep = filterProcessor.applyOcrPreparation(bitmap)
        return ocrProcessor.extractText(ocrPrep)
    }

    /**
     * Bước 4: Tạo PDF
     */
    fun createPdf(bitmap: Bitmap, fileName: String): File? {
        return pdfProcessor.createPdfFromBitmaps(listOf(bitmap), fileName)
    }

    // Giữ lại hàm cũ để không làm hỏng các phần khác, nhưng gọi các hàm con
    suspend fun fullProcess(bitmap: Bitmap, pdfFileName: String = "ScanLink_Export"): ScanResult {
        val (transformed, detected) = transformDocument(bitmap)
        val filtered = applyFilters(transformed)
        val text = extractText(transformed) // Dùng bản đã cắt nhưng chưa lọc B&W để OCR tốt hơn
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
            applyFilters(transformed)
        }

        return pdfProcessor.createPdfFromBitmaps(processedBitmaps, pdfFileName)
    }
}
