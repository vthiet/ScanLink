package com.example.scanlink.features.document_scanner.data.opencv

import android.graphics.Bitmap
import com.example.scanlink.features.document_scanner.domain.entities.ScanPoint
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import kotlin.math.max
import kotlin.math.sqrt

class PerspectiveTransformer {

    fun transform(bitmap: Bitmap, points: List<ScanPoint>): Bitmap {
        val src = Mat()
        Utils.bitmapToMat(bitmap, src)

        // 1. Sắp xếp điểm chuẩn: Top-Left, Top-Right, Bottom-Right, Bottom-Left
        val ordered = sortPoints(points)
        val tl = Point(ordered[0].x, ordered[0].y)
        val tr = Point(ordered[1].x, ordered[1].y)
        val br = Point(ordered[2].x, ordered[2].y)
        val bl = Point(ordered[3].x, ordered[3].y)

        // 2. Tính toán kích thước thực tế của tài liệu sau khi phẳng hóa
        val widthA = sqrt(((br.x - bl.x) * (br.x - bl.x)) + ((br.y - bl.y) * (br.y - bl.y)))
        val widthB = sqrt(((tr.x - tl.x) * (tr.x - tl.x)) + ((tr.y - tl.y) * (tr.y - tl.y)))
        val maxWidth = max(widthA, widthB).toInt().coerceAtLeast(100)

        val heightA = sqrt(((tr.x - br.x) * (tr.x - br.x)) + ((tr.y - br.y) * (tr.y - br.y)))
        val heightB = sqrt(((tl.x - bl.x) * (tl.x - bl.x)) + ((tl.y - bl.y) * (tl.y - bl.y)))
        val maxHeight = max(heightA, heightB).toInt().coerceAtLeast(100)

        // 3. Thiết lập các điểm nguồn và điểm đích
        val srcPoints = MatOfPoint2f(tl, tr, br, bl)
        val dstPoints = MatOfPoint2f(
            Point(0.0, 0.0),
            Point(maxWidth.toDouble(), 0.0),
            Point(maxWidth.toDouble(), maxHeight.toDouble()),
            Point(0.0, maxHeight.toDouble())
        )

        // 4. Thực hiện Warp Perspective
        val matrix = Imgproc.getPerspectiveTransform(srcPoints, dstPoints)
        val warped = Mat()
        Imgproc.warpPerspective(src, warped, matrix, Size(maxWidth.toDouble(), maxHeight.toDouble()))

        // 5. Chuyển về Bitmap
        val resultBitmap = Bitmap.createBitmap(warped.cols(), warped.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(warped, resultBitmap)

        // Giải phóng bộ nhớ Mat
        src.release()
        warped.release()
        matrix.release()

        return resultBitmap
    }

    private fun sortPoints(points: List<ScanPoint>): List<ScanPoint> {
        // Thuật toán sắp xếp 4 góc bền bỉ:
        // Top-Left: Tổng (x+y) nhỏ nhất
        // Bottom-Right: Tổng (x+y) lớn nhất
        // Top-Right: Hiệu (x-y) lớn nhất
        // Bottom-Left: Hiệu (x-y) nhỏ nhất
        
        val sortedBySum = points.sortedBy { it.x + it.y }
        val tl = sortedBySum.first()
        val br = sortedBySum.last()

        val remaining = points.filter { it != tl && it != br }
        val tr = remaining.maxBy { it.x - it.y }
        val bl = remaining.minBy { it.x - it.y }

        return listOf(tl, tr, br, bl)
    }
}
