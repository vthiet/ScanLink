package com.example.scanlink.features.document_scanner.data.pdf

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PDFProcessor {

    private fun scaleAndCompressBitmap(bitmap: Bitmap): Bitmap {
        val maxDimension = 600
        val width = bitmap.width
        val height = bitmap.height

        // 1. Giảm độ phân giải nếu ảnh quá lớn
        val scaledBitmap = if (width > maxDimension || height > maxDimension) {
            val ratio = width.toFloat() / height.toFloat()
            val newWidth = if (ratio > 1) maxDimension else (maxDimension * ratio).toInt()
            val newHeight = if (ratio > 1) (maxDimension / ratio).toInt() else maxDimension
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }

        // 2. Chuyển đổi cấu hình sang RGB_565 để giảm 1 nửa dung lượng thô
        val configBitmap = scaledBitmap.copy(Bitmap.Config.RGB_565, false) ?: scaledBitmap

        // 3. Nén dung lượng qua định dạng JPEG (chất lượng 40%)
        val outputStream = ByteArrayOutputStream()
        configBitmap.compress(Bitmap.CompressFormat.JPEG, 40, outputStream)
        val byteArray = outputStream.toByteArray()

        // 4. Giải mã lại bằng cấu hình RGB_565
        val options = BitmapFactory.Options().apply {
            inPreferredConfig = Bitmap.Config.RGB_565
        }
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options) ?: configBitmap
    }

    fun createPdfFromBitmaps(bitmaps: List<Bitmap>, fileName: String): File? {
        if (bitmaps.isEmpty()) return null

        val pdfDocument = PdfDocument()

        try {
            bitmaps.forEachIndexed { index, rawBitmap ->
                // Nén và giảm độ phân giải của bitmap trước khi vẽ vào PDF
                val bitmap = scaleAndCompressBitmap(rawBitmap)

                // Tạo thông tin trang (kích thước trang khớp với kích thước ảnh đã nén)
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
