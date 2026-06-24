package com.example.scanlink.features.document_scanner.data.opencv

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.entities.ScanPoint
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class DocumentDetector {

    fun detectDocument(bitmap: Bitmap): List<ScanPoint>? {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)

        // 1. Resize để xử lý nhanh
        val targetHeight = 500.0
        val ratio = src.rows().toDouble() / targetHeight
        val downscaled = Mat()
        Imgproc.resize(src, downscaled, Size((src.cols() / ratio), targetHeight))

        // 2. Tiền xử lý nâng cao cho môi trường thực tế
        val gray = Mat()
        Imgproc.cvtColor(downscaled, gray, Imgproc.COLOR_BGR2GRAY)
        
        // CẢI TIẾN: Sử dụng CLAHE để làm rõ cạnh giấy trong vùng tối/đổ bóng
        val contrast = Mat()
        val clahe = Imgproc.createCLAHE(2.0, Size(8.0, 8.0))
        clahe.apply(gray, contrast)
        
        val blur = Mat()
        Imgproc.GaussianBlur(contrast, blur, Size(5.0, 5.0), 0.0)
        
        // CẢI TIẾN: Giảm ngưỡng Canny để bắt được các cạnh yếu (giấy trắng trên nền xám)
        val edges = Mat()
        Imgproc.Canny(blur, edges, 50.0, 150.0)
        
        // CẢI TIẾN: Dùng MORPH_CLOSE để nối liền các nét đứt của khung giấy
        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(5.0, 5.0))
        Imgproc.morphologyEx(edges, edges, Imgproc.MORPH_CLOSE, kernel)

        // 3. Tìm contours
        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(
            edges,
            contours,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        val sortedContours = contours.sortedByDescending { Imgproc.contourArea(it) }.take(10)

        var bestApprox: MatOfPoint2f? = null
        val downscaledArea = downscaled.cols() * downscaled.rows()

        for (c in sortedContours) {
            val c2f = MatOfPoint2f(*c.toArray())
            val peri = Imgproc.arcLength(c2f, true)
            val approx = MatOfPoint2f()
            
            // Thử xấp xỉ đa giác
            Imgproc.approxPolyDP(c2f, approx, 0.02 * peri, true)

            val area = Imgproc.contourArea(approx)
            
            // Điều kiện: Hình tứ giác và chiếm ít nhất 15% diện tích (giảm từ 20% để bắt giấy từ xa)
            if (approx.total() == 4L && area > (downscaledArea * 0.15)) {
                // Kiểm tra thêm độ lồi (convexity) để loại bỏ các hình méo mó
                if (Imgproc.isContourConvex(MatOfPoint(*approx.toArray()))) {
                    bestApprox = approx
                    break
                }
            }
        }

        // Giải phóng
        src.release()
        downscaled.release()
        gray.release()
        contrast.release()
        blur.release()
        edges.release()
        kernel.release()

        return if (bestApprox != null) {
            val points = bestApprox.toArray().map { 
                Point(it.x * ratio, it.y * ratio) 
            }.toTypedArray()
            orderPoints(points)
        } else {
            null
        }
    }

    private fun orderPoints(points: Array<Point>): List<ScanPoint> {
        val sortedBySum = points.sortedBy { it.x + it.y }
        val topLeft = sortedBySum.first()
        val bottomRight = sortedBySum.last()

        val remaining = points.filter { it != topLeft && it != bottomRight }
        val topRight = remaining.maxByOrNull { it.x - it.y } ?: points[0]
        val bottomLeft = remaining.minByOrNull { it.x - it.y } ?: points[0]

        return listOf(
            ScanPoint(topLeft.x, topLeft.y),
            ScanPoint(topRight.x, topRight.y),
            ScanPoint(bottomRight.x, bottomRight.y),
            ScanPoint(bottomLeft.x, bottomLeft.y)
        )
    }
}
