package com.example.scanlink.features.document_scanner.data.pdf

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PDFProcessor {

    fun createPdfFromBitmaps(bitmaps: List<Bitmap>, fileName: String): File? {
        if (bitmaps.isEmpty()) return null

        val pdfDocument = PdfDocument()

        try {
            bitmaps.forEachIndexed { index, bitmap ->
                // Tạo thông tin trang (kích thước trang khớp với kích thước ảnh)
                val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, index + 1).create()
                
                // Bắt đầu một trang mới
                val page = pdfDocument.startPage(pageInfo)
                
                // Vẽ bitmap lên trang PDF
                val canvas: Canvas = page.canvas
                canvas.drawBitmap(bitmap, 0f, 0f, null)
                
                // Kết thúc trang
                pdfDocument.finishPage(page)
            }

            // Đường dẫn lưu file: Thư mục Documents công cộng của thiết bị
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs()
            }

            val pdfFile = File(downloadsDir, "$fileName.pdf")
            val outputStream = FileOutputStream(pdfFile)
            
            pdfDocument.writeTo(outputStream)
            
            outputStream.close()
            return pdfFile

        } catch (e: IOException) {
            e.printStackTrace()
            return null
        } finally {
            pdfDocument.close()
        }
    }
}
