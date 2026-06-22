//package com.example.scanlink.features.document_scanner.domain.usecases
//
//import android.graphics.Bitmap
//import com.example.scanlink.features.document_scanner.data.ocr.OCRProcessor
//
//class ExtractTextUseCase(
//
//    private val ocrProcessor: OCRProcessor
//
//) {
//
//    suspend operator fun invoke(
//        bitmap: Bitmap
//    ): String {
//
//        return ocrProcessor.extractText(bitmap)
//    }
//}