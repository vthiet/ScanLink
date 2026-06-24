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
        
        val (displayBitmap, ocrBitmap, detected) = if (points != null) {
            val transformed = transformer.transform(bitmap, points)
            
            // Bản 1: Chuyển Đen-Trắng để xuất PDF và hiển thị UI
            val bw = filterProcessor.applyBlackWhite(transformed)
            
            // Bản 2: Sử dụng bộ lọc cao cấp tối ưu riêng cho OCR (Upscaling + Denoise)
            val ocrPrep = filterProcessor.applyOcrPreparation(transformed)
            
            Triple(bw, ocrPrep, true)
            
        } else {
            // Nếu không tìm thấy khung, xử lý trên toàn bộ ảnh
            val bw = filterProcessor.applyBlackWhite(bitmap)
            val ocrPrep = filterProcessor.applyOcrPreparation(bitmap)
            Triple(bw, ocrPrep, false)
        }

        // 2. Trích xuất chữ từ ảnh đã được tối ưu hóa siêu sắc nét
        val textResult = ocrProcessor.extractText(ocrBitmap)

        // 3. Xuất PDF từ ảnh Đen Trắng
        val pdfFile = pdfProcessor.createPdfFromBitmaps(listOf(displayBitmap), pdfFileName)

        return ScanResult(
            originalBitmap = bitmap,
            processedBitmap = displayBitmap,
            extractedText = textResult,
            pdfFile = pdfFile,
            isDocumentDetected = detected
        )
    }

    /**
     * Xử lý nhiều ảnh cùng lúc và gộp thành 1 file PDF duy nhất
     */
    suspend fun processMultipleImages(
        bitmaps: List<Bitmap>,
        pdfFileName: String = "ScanLink_Batch_Export"
    ): File? {
        if (bitmaps.isEmpty()) return null

        val processedBitmaps = bitmaps.map { bitmap ->
            // Thử nhận diện tài liệu cho từng ảnh
            val points = detector.detectDocument(bitmap)
            val transformed = if (points != null) {
                transformer.transform(bitmap, points)
            } else {
                bitmap
            }

            // Áp dụng bộ lọc Đen-Trắng để PDF đồng nhất và chuyên nghiệp
            filterProcessor.applyBlackWhite(transformed)
        }

        // Tạo 1 file PDF duy nhất chứa tất cả các trang
        return pdfProcessor.createPdfFromBitmaps(processedBitmaps, pdfFileName)
    }
}
