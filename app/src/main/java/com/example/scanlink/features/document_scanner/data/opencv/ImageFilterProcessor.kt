package com.example.scanlink.features.document_scanner.data.opencv

import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class ImageFilterProcessor {

    /**
     * Tối ưu ảnh dành riêng cho bộ máy OCR nhận diện chữ.
     */
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

    /**
     * Bộ lọc B&W: Trắng đen hoàn toàn, xóa sạch nền xám, độ tương phản cực cao.
     * Giúp văn bản trông như được in ra từ máy photocopy.
     */
    fun applyBlackWhite(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)

        // 1. Tăng mạnh độ tương phản trước khi nhị phân hóa để chữ đậm hơn
        val contrast = Mat()
        gray.convertTo(contrast, -1, 1.6, -60.0)

        // 2. Sử dụng ngưỡng thích nghi để tạo nền trắng sạch tuyệt đối
        val bw = Mat()
        Imgproc.adaptiveThreshold(contrast, bw, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 21, 10.0)

        val result = Bitmap.createBitmap(bw.cols(), bw.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(bw, result)
        src.release(); gray.release(); contrast.release(); bw.release()
        return result
    }

    /**
     * Bộ lọc Grayscale: Chuyển sang tông màu xám mịn màng.
     * Thích hợp cho tài liệu có hình ảnh minh họa.
     */
    fun applyGrayscale(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        val gray = Mat()
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)
        
        // Làm mịn và tăng độ tương phản nhẹ cho tông xám trông sạch hơn
        val finalMat = Mat()
        gray.convertTo(finalMat, -1, 1.1, 0.0)
        
        val result = Bitmap.createBitmap(finalMat.cols(), finalMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(finalMat, result)
        src.release(); gray.release(); finalMat.release()
        return result
    }

    /**
     * Bộ lọc Magic Color: Làm trắng tinh nền giấy nhưng giữ màu chữ rực rỡ.
     * Rất rõ rệt với con dấu đỏ hoặc chữ ký xanh.
     */
    fun applyMagicColor(bitmap: Bitmap): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)
        
        val rgb = Mat()
        Imgproc.cvtColor(src, rgb, Imgproc.COLOR_RGBA2RGB)
        
        // 1. Ước tính phông nền mạnh để xóa sạch bóng đổ và vết bẩn
        val blurred = Mat()
        Imgproc.GaussianBlur(rgb, blurred, Size(51.0, 51.0), 0.0)
        
        // 2. Chia ảnh gốc cho phông nền để làm trắng giấy (kỹ thuật Division)
        val flattened = Mat()
        Core.divide(rgb, blurred, flattened, 255.0)
        
        // 3. Tăng độ bão hòa màu sắc (Saturation) để các chi tiết màu nổi bật hẳn lên
        val hsv = Mat()
        Imgproc.cvtColor(flattened, hsv, Imgproc.COLOR_RGB2HSV)
        val channels = mutableListOf<Mat>()
        Core.split(hsv, channels)
        
        // Đẩy màu chữ ký, con dấu lên rực rỡ hơn 60%
        channels[1].convertTo(channels[1], -1, 1.6, 0.0)
        
        val finalHsv = Mat()
        Core.merge(channels, finalHsv)
        
        val finalRgb = Mat()
        Imgproc.cvtColor(finalHsv, finalRgb, Imgproc.COLOR_HSV2RGB)
        
        // Tăng độ sáng và tương phản cuối cùng cho ảnh sắc sảo
        val finalMat = Mat()
        finalRgb.convertTo(finalMat, -1, 1.2, 5.0)
        
        val result = Bitmap.createBitmap(finalMat.cols(), finalMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(finalMat, result)
        
        src.release(); rgb.release(); blurred.release(); flattened.release()
        hsv.release(); finalHsv.release(); finalRgb.release(); finalMat.release()
        channels.forEach { it.release() }
        
        return result
    }

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
