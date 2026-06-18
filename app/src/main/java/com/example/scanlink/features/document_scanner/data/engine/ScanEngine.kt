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

    suspend fun fullProcess(bitmap: Bitmap, pdfFileName: String = "ScanLink_Export"): ScanResult {
        // 1. Nhận diện vùng giấy
        val points = detector.detectDocument(bitmap)
        
        val (finalBitmap, detected) = if (points != null) {
            val transformed = transformer.transform(bitmap, points)
            
            // CẢI TIẾN CHO GIẤY THẬT: 
            // Bước 1: Làm sắc nét các nét chữ bị mờ do rung tay hoặc lấy nét kém
            val sharpened = filterProcessor.applySharpen(transformed)
            // Bước 2: Áp dụng bộ lọc Đen-Trắng nâng cao (có khử nhiễu và CLAHE)
            filterProcessor.applyBlackWhite(sharpened) to true
            
        } else {
            // Nếu không tìm thấy khung, vẫn làm nét và lọc trên toàn bộ ảnh
            val sharpened = filterProcessor.applySharpen(bitmap)
            filterProcessor.applyBlackWhite(sharpened) to false
        }

        // 2. Trích xuất chữ (Đã được cập nhật bộ Tiếng Việt trong OCRProcessor)
        val textResult = ocrProcessor.extractText(finalBitmap)

        // 3. Xuất PDF
        val pdfFile = pdfProcessor.createPdfFromBitmaps(listOf(finalBitmap), pdfFileName)

        return ScanResult(
            originalBitmap = bitmap,
            processedBitmap = finalBitmap,
            extractedText = textResult,
            pdfFile = pdfFile,
            isDocumentDetected = detected
        )
    }
}
