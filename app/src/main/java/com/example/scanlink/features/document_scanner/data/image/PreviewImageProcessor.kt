package com.example.scanlink.features.document_scanner.data.image

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import com.example.scanlink.features.document_scanner.domain.entities.CropRect
import java.io.IOException

object PreviewImageProcessor {

    fun loadBitmap(
        context: Context,
        uri: Uri
    ): Bitmap {

        val bitmap =
            context.contentResolver
                .openInputStream(uri)
                .use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }
                ?: throw IOException("Không thể đọc ảnh preview.")

        val orientation =
            context.contentResolver
                .openInputStream(uri)
                .use { inputStream ->
                    ExifInterface(inputStream!!)
                        .getAttributeInt(
                            ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL
                        )
                }

        val matrix = Matrix()

        when (orientation) {

            ExifInterface.ORIENTATION_ROTATE_90 ->
                matrix.postRotate(90f)

            ExifInterface.ORIENTATION_ROTATE_180 ->
                matrix.postRotate(180f)

            ExifInterface.ORIENTATION_ROTATE_270 ->
                matrix.postRotate(270f)

            else ->
                return bitmap
        }

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    fun transform(
        bitmap: Bitmap,
        rotation: Float,
        flipHorizontal: Boolean,
        flipVertical: Boolean,
        cropCenter: Boolean
    ): Bitmap {
        val source = if (cropCenter) cropCenter(bitmap) else bitmap

        val matrix = Matrix().apply {
            postRotate(rotation)
            postScale(
                if (flipHorizontal) -1f else 1f,
                if (flipVertical) -1f else 1f
            )
        }

        return Bitmap.createBitmap(
            source,
            0,
            0,
            source.width,
            source.height,
            matrix,
            true
        )
    }

    private fun cropCenter(bitmap: Bitmap): Bitmap {
        val targetWidth = (bitmap.width * 0.82f).toInt().coerceAtLeast(1)
        val targetHeight = (bitmap.height * 0.82f).toInt().coerceAtLeast(1)
        val left = ((bitmap.width - targetWidth) / 2).coerceAtLeast(0)
        val top = ((bitmap.height - targetHeight) / 2).coerceAtLeast(0)

        return Bitmap.createBitmap(
            bitmap,
            left,
            top,
            targetWidth,
            targetHeight
        )
    }

    fun saveToPictures(
        context: Context,
        bitmap: Bitmap,
        fileName: String
    ): Uri {
        val resolver = context.contentResolver
        val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/ScanLink")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(imageCollection, values)
            ?: throw IOException("Không thể tạo file ảnh.")

        resolver.openOutputStream(uri).use { outputStream ->
            if (outputStream == null) throw IOException("Không thể mở file ảnh.")
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }

        return uri
    }
    fun cropByRect(
        bitmap: Bitmap,
        cropRect: CropRect
    ): Bitmap {
        val left = (bitmap.width * cropRect.left).toInt().coerceIn(0, bitmap.width - 1)
        val top = (bitmap.height * cropRect.top).toInt().coerceIn(0, bitmap.height - 1)
        val right = (bitmap.width * cropRect.right).toInt().coerceIn(left + 1, bitmap.width)
        val bottom = (bitmap.height * cropRect.bottom).toInt().coerceIn(top + 1, bitmap.height)

        return Bitmap.createBitmap(
            bitmap,
            left,
            top,
            right - left,
            bottom - top
        )
    }
}
