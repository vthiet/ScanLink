package com.example.scanlink.features.document_scanner.data.opencv

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class ImageFilterProcessor {

    fun applyGrayScale(
        bitmap: Bitmap
    ): Bitmap {

        val src = Mat()

        Utils.bitmapToMat(bitmap, src)

        val gray = Mat()

        Imgproc.cvtColor(
            src,
            gray,
            Imgproc.COLOR_BGR2GRAY
        )

        val resultBitmap =
            Bitmap.createBitmap(
                gray.cols(),
                gray.rows(),
                Bitmap.Config.ARGB_8888
            )

        Utils.matToBitmap(
            gray,
            resultBitmap
        )

        src.release()
        gray.release()

        return resultBitmap
    }

    fun applyBlackWhite(
        bitmap: Bitmap
    ): Bitmap {

        val src = Mat()
        Utils.bitmapToMat(bitmap, src)

        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)

        // 1. Khử nhiễu hạt (Denoising) - Rất quan trọng cho ảnh chụp từ camera
        val blurred = Mat()
        Imgproc.GaussianBlur(gray, blurred, Size(3.0, 3.0), 0.0)

        // 2. Tăng cường độ tương phản cục bộ (CLAHE)
        // Giúp xử lý các vùng bị đổ bóng hoặc ánh sáng không đều trên giấy thật
        val contrast = Mat()
        val clahe = Imgproc.createCLAHE(2.0, Size(8.0, 8.0))
        clahe.apply(blurred, contrast)

        val bw = Mat()
        // 3. Adaptive Threshold với tham số tối ưu cho giấy thật
        // blockSize = 31: Nhìn rộng hơn để phân biệt nền giấy và chữ
        // C = 10: Lọc bỏ nhiễu xám tốt hơn
        Imgproc.adaptiveThreshold(
            contrast,
            bw,
            255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY,
            31,
            10.0
        )

        val resultBitmap =
            Bitmap.createBitmap(
                bw.cols(),
                bw.rows(),
                Bitmap.Config.ARGB_8888
            )

        Utils.matToBitmap(
            bw,
            resultBitmap
        )

        src.release()
        gray.release()
        blurred.release()
        contrast.release()
        bw.release()

        return resultBitmap
    }

    fun applyMagicColor(bitmap: Bitmap): Bitmap {

        val src = Mat()
        Utils.bitmapToMat(bitmap, src)

        val normalized = Mat()
        Core.normalize(src, normalized, 0.0, 255.0, Core.NORM_MINMAX)

        val lab = Mat()
        Imgproc.cvtColor(normalized, lab, Imgproc.COLOR_BGR2Lab)

        val channels = ArrayList<Mat>()
        Core.split(lab, channels)

        val clahe = Imgproc.createCLAHE()
        clahe.setClipLimit(3.0)
        clahe.apply(channels[0], channels[0])

        Core.merge(channels, lab)

        val enhanced = Mat()
        Imgproc.cvtColor(lab, enhanced, Imgproc.COLOR_Lab2BGR)

        val kernel = Mat(3, 3, CvType.CV_32F)
        kernel.put(
            0, 0,
            0.0, -1.0, 0.0,
            -1.0, 5.0, -1.0,
            0.0, -1.0, 0.0
        )

        val final = Mat()
        Imgproc.filter2D(enhanced, final, -1, kernel)

        val result = Bitmap.createBitmap(
            final.cols(),
            final.rows(),
            Bitmap.Config.ARGB_8888
        )

        Utils.matToBitmap(final, result)

        src.release()
        normalized.release()
        lab.release()
        enhanced.release()
        final.release()
        channels.forEach { it.release() }
        kernel.release()

        return result
    }


    fun applySharpen(
        bitmap: Bitmap
    ): Bitmap {

        val src = Mat()

        Utils.bitmapToMat(bitmap, src)

        val sharpened = Mat()

        val kernel = Mat(
            3,
            3,
            CvType.CV_32F
        )

        kernel.put(
            0,
            0,
            0.0, -1.0, 0.0,
            -1.0, 5.0, -1.0,
            0.0, -1.0, 0.0
        )

        Imgproc.filter2D(
            src,
            sharpened,
            -1,
            kernel
        )

        val resultBitmap =
            Bitmap.createBitmap(
                sharpened.cols(),
                sharpened.rows(),
                Bitmap.Config.ARGB_8888
            )

        Utils.matToBitmap(
            sharpened,
            resultBitmap
        )

        src.release()
        sharpened.release()

        return resultBitmap
    }
}