package com.example.scanlink.features.document_scanner.data.opencv

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class ImageFilterProcessor {

    fun applyOcrPreparation(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)

        // 1. Khử nhiễu nhưng giữ lại độ sắc nét của cạnh chữ (quan trọng để tránh mất nét 'g', 'y', 'i')
        val denoised = Mat()
        Imgproc.bilateralFilter(gray, denoised, 5, 75.0, 75.0)

        // 2. Cân bằng ánh sáng cục bộ nhẹ để xử lý bóng đổ trên trang sách
        val clahe = Imgproc.createCLAHE(1.5, Size(8.0, 8.0))
        val finalMat = Mat()
        clahe.apply(denoised, finalMat)

        val result = Bitmap.createBitmap(finalMat.cols(), finalMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(finalMat, result)
        
        src.release(); gray.release(); denoised.release(); finalMat.release()
        return result
    }

    /**
     * Tạo ảnh Đen-Trắng (B&W) sạch cho PDF.
     */
    fun applyBlackWhite(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY)

        val bw = Mat()
        // Dùng Gaussian Adaptive Threshold để tạo nền trắng sạch
        Imgproc.adaptiveThreshold(gray, bw, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 31, 15.0)

        val result = Bitmap.createBitmap(bw.cols(), bw.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(bw, result)
        src.release(); gray.release(); bw.release()
        return result
    }

    // Hàm phụ trợ làm sắc nét cho hiển thị
    fun applySharpen(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        val sharpened = Mat()
        val kernel = Mat(3, 3, CvType.CV_32F)
        kernel.put(0, 0, 0.0, -0.5, 0.0, -0.5, 3.0, -0.5, 0.0, -0.5, 0.0)
        Imgproc.filter2D(src, sharpened, -1, kernel)
        val result = Bitmap.createBitmap(sharpened.cols(), sharpened.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(sharpened, result)
        src.release(); sharpened.release(); kernel.release()
        return result
    }
}
