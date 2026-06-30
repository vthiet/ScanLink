package com.example.scanlink.features.document_scanner.data.repositories

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
import com.example.scanlink.features.document_scanner.domain.repositories.IDocumentExportRepository
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import kotlin.math.min

class DocumentExportRepositoryImpl @Inject constructor() : IDocumentExportRepository {
    private companion object {
        const val A4_WIDTH = 595
        const val A4_HEIGHT = 842
        const val PAGE_MARGIN = 32f
        const val MAX_BITMAP_DIMENSION = 700
    }

    override fun createPdfFromImageUris(
        context: Context,
        imageUris: List<String>,
        fileName: String
    ): File {
        require(imageUris.isNotEmpty()) { "imageUris must not be empty" }

        val pdfDocument = PdfDocument()
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

        try {
            imageUris.forEachIndexed { index, imageUri ->
                val bitmap = decodeBitmap(context, Uri.parse(imageUri))
                    ?: throw IllegalArgumentException("Cannot decode image: $imageUri")

                val pageInfo = PdfDocument.PageInfo.Builder(A4_WIDTH, A4_HEIGHT, index + 1).create()
                val page = pdfDocument.startPage(pageInfo)
                val canvas = page.canvas

                val availableWidth = A4_WIDTH - PAGE_MARGIN * 2
                val availableHeight = A4_HEIGHT - PAGE_MARGIN * 2
                val scale = min(
                    availableWidth / bitmap.width.toFloat(),
                    availableHeight / bitmap.height.toFloat()
                )
                val targetWidth = bitmap.width * scale
                val targetHeight = bitmap.height * scale
                val left = (A4_WIDTH - targetWidth) / 2f
                val top = (A4_HEIGHT - targetHeight) / 2f

                canvas.drawBitmap(
                    bitmap,
                    null,
                    RectF(left, top, left + targetWidth, top + targetHeight),
                    paint
                )
                pdfDocument.finishPage(page)
                bitmap.recycle()
            }

            val outputDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                ?: context.filesDir
            if (!outputDir.exists()) outputDir.mkdirs()

            val normalizedFileName = if (fileName.endsWith(".pdf", ignoreCase = true)) {
                fileName
            } else {
                "$fileName.pdf"
            }
            val outputFile = File(outputDir, normalizedFileName)

            FileOutputStream(outputFile).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }

            return outputFile
        } finally {
            pdfDocument.close()
        }
    }

    private fun decodeBitmap(context: Context, uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, bounds)
        }

        val options = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight)
            inPreferredConfig = Bitmap.Config.RGB_565
        }

        val decoded = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, options)
        } ?: return null

        return rotateImageIfRequired(context, decoded, uri)
    }

    private fun calculateInSampleSize(width: Int, height: Int): Int {
        var sampleSize = 1
        var scaledWidth = width
        var scaledHeight = height

        while (scaledWidth / 2 >= MAX_BITMAP_DIMENSION || scaledHeight / 2 >= MAX_BITMAP_DIMENSION) {
            sampleSize *= 2
            scaledWidth /= 2
            scaledHeight /= 2
        }

        return sampleSize.coerceAtLeast(1)
    }

    private fun rotateImageIfRequired(context: Context, bitmap: Bitmap, uri: Uri): Bitmap {
        val exif = context.contentResolver.openInputStream(uri)?.use { inputStream ->
            ExifInterface(inputStream)
        } ?: return bitmap

        val degrees = when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }

        if (degrees == 0f) return bitmap

        val matrix = Matrix().apply { postRotate(degrees) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated != bitmap) bitmap.recycle()
        return rotated
    }
}
