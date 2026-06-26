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
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)

        val denoised = Mat()
        Imgproc.bilateralFilter(gray, denoised, 5, 75.0, 75.0)

        val clahe = Imgproc.createCLAHE(1.5, Size(8.0, 8.0))
        val finalMat = Mat()
        clahe.apply(denoised, finalMat)

        val result = Bitmap.createBitmap(finalMat.cols(), finalMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(finalMat, result)
        
        src.release(); gray.release(); denoised.release(); finalMat.release()
        return result
    }

    fun applyBlackWhite(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)

        val contrast = Mat()
        gray.convertTo(contrast, -1, 1.6, -60.0)

        val bw = Mat()
        Imgproc.adaptiveThreshold(contrast, bw, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 21, 10.0)

        val result = Bitmap.createBitmap(bw.cols(), bw.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(bw, result)
        src.release(); gray.release(); contrast.release(); bw.release()
        return result
    }

    fun applyGrayscale(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)
        
        val finalMat = Mat()
        gray.convertTo(finalMat, -1, 1.1, 0.0)
        
        val result = Bitmap.createBitmap(finalMat.cols(), finalMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(finalMat, result)
        src.release(); gray.release(); finalMat.release()
        return result
    }

    fun applyMagicColor(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        val rgb = Mat()
        Imgproc.cvtColor(src, rgb, Imgproc.COLOR_RGBA2RGB)
        
        val blurred = Mat()
        Imgproc.GaussianBlur(rgb, blurred, Size(51.0, 51.0), 0.0)
        
        val flattened = Mat()
        Core.divide(rgb, blurred, flattened, 255.0)
        
        val hsv = Mat()
        Imgproc.cvtColor(flattened, hsv, Imgproc.COLOR_RGB2HSV)
        val channels = mutableListOf<Mat>()
        Core.split(hsv, channels)
        channels[1].convertTo(channels[1], -1, 1.6, 0.0)
        
        val finalHsv = Mat()
        Core.merge(channels, finalHsv)
        val finalRgb = Mat()
        Imgproc.cvtColor(finalHsv, finalRgb, Imgproc.COLOR_HSV2RGB)
        
        val finalMat = Mat()
        finalRgb.convertTo(finalMat, -1, 1.2, 5.0)
        
        val result = Bitmap.createBitmap(finalMat.cols(), finalMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(finalMat, result)
        
        src.release(); rgb.release(); blurred.release(); flattened.release()
        hsv.release(); finalHsv.release(); finalRgb.release(); finalMat.release()
        channels.forEach { it.release() }
        return result
    }
}
