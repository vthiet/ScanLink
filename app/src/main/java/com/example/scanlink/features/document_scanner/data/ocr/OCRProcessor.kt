package com.example.scanlink.features.document_scanner.data.ocr

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.tasks.await

class OCRProcessor {

    suspend fun extractText(bitmap: Bitmap): String {
        return try {

            val recognizer = TextRecognition.getClient(
                TextRecognizerOptions.DEFAULT_OPTIONS
            )

            val image = InputImage.fromBitmap(bitmap, 0)

            val result = recognizer.process(image).await()

            if (result.text.isBlank()) {
                return "Không tìm thấy nội dung chữ."
            }

            var text = result.text

            // Ghép các từ bị ngắt dòng bởi dấu gạch nối
            text = text.replace(
                Regex("(\\w+)[\\-\u00AD\u2010\u2011\u2012\u2013\u2014\u2015]\\s*\\n\\s*(\\w+)"),
                "$1$2"
            )

            text.trim()

        } catch (e: Exception) {
            "Lỗi nhận diện: ${e.message}"
        }
    }
}
