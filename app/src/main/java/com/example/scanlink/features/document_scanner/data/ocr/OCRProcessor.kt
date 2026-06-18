package com.example.scanlink.features.document_scanner.data.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

class OCRProcessor {

    /**
     * Trích xuất văn bản tiếng Việt từ Bitmap.
     * Sử dụng ML Kit Vietnamese Text Recognition - bộ engine tốt nhất hiện nay.
     */
    suspend fun extractText(bitmap: Bitmap): String {
        return try {
            val recognizer = TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )
            val image = InputImage.fromBitmap(bitmap, 0)
            
            val result = recognizer.process(image).await()
            
            // Xử lý logic sắp xếp để văn bản không bị lộn xộn khi chụp thực tế
            val textBlocks = result.textBlocks
            if (textBlocks.isEmpty()) return ""

            // Sắp xếp các khối văn bản từ trên xuống dưới, rồi từ trái qua phải
            // Điều này rất quan trọng khi scan danh sách hoặc bảng biểu
            val sortedBlocks = textBlocks.sortedWith(compareBy({ it.boundingBox?.top ?: 0 }, { it.boundingBox?.left ?: 0 }))

            val extractedText = StringBuilder()
            for (block in sortedBlocks) {
                extractedText.append(block.text).append("\n\n")
            }

            extractedText.toString().trim()

        } catch (e: Exception) {
            e.printStackTrace()
            "Lỗi nhận diện: ${e.message}"
        }
    }
}
