package com.example.scanlink.features.document_scanner.data.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

class OCRProcessor {

    suspend fun extractText(bitmap: Bitmap): String {
        return try {
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            val image = InputImage.fromBitmap(bitmap, 0)
            val result = recognizer.process(image).await()

            if (result.text.isBlank()) return "Không tìm thấy nội dung chữ."

            val sb = StringBuilder()
            for (block in result.textBlocks) {
                for (line in block.lines) {
                    sb.append(line.text).append("\n")
                }
                sb.append("\n")
            }

            var text = sb.toString()

            // 4. DỌN DẸP DẤU CÂU NHIỄU (Loại bỏ các dấu : . đứng lẻ loi do hạt nhiễu)
            text = text.replace(Regex("(?<=\\w)[:\\.]{1,2}(?=\\s|$)"), "")

            text.trim()
        } catch (e: Exception) {
            "Lỗi nhận diện: ${e.message}"
        }
    }
}
