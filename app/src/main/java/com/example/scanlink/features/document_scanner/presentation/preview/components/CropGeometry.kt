package com.example.scanlink.features.document_scanner.presentation.preview.components

import android.graphics.Rect as BitmapRect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import com.example.scanlink.features.document_scanner.domain.entities.CropRect
import kotlin.math.roundToInt

enum class CropHandlePosition {
    TopLeft,
    TopRight,
    BottomLeft,
    BottomRight
}

data class CropGeometry(
    val imageRect: Rect,
    val viewportRect: Rect,
    val cropBounds: Rect,
    val cropRectOnScreen: Rect,
    val minCropWidthPx: Float,
    val minCropHeightPx: Float
) {
    fun moveCropRect(
        startRect: Rect,
        drag: Offset
    ): CropRect {
        return startRect
            .translateBy(drag)
            .constrainTo(
                bounds = cropBounds,
                minWidth = minCropWidthPx,
                minHeight = minCropHeightPx
            )
            .toCropRect(imageRect)
    }

    fun resizeCropRect(
        startRect: Rect,
        position: CropHandlePosition,
        drag: Offset
    ): CropRect {
        return startRect
            .resize(
                position = position,
                drag = drag,
                bounds = cropBounds,
                minWidth = minCropWidthPx,
                minHeight = minCropHeightPx
            )
            .toCropRect(imageRect)
    }
}

@Composable
fun rememberCropGeometry(
    cropRect: CropRect,
    imageRect: Rect,
    viewportRect: Rect,
    minCropSizePx: Float
): CropGeometry {
    return remember(cropRect, imageRect, viewportRect, minCropSizePx) {
        createCropGeometry(
            cropRect = cropRect,
            imageRect = imageRect,
            viewportRect = viewportRect,
            minCropSizePx = minCropSizePx
        )
    }
}

fun mapCropRectToBitmapRect(
    cropRectInViewport: Rect,
    imageRectInViewport: Rect,
    bitmapWidth: Int,
    bitmapHeight: Int
): BitmapRect {
    val safeCropRect = cropRectInViewport.constrainTo(
        bounds = imageRectInViewport,
        minWidth = 1f,
        minHeight = 1f
    )

    val left = (((safeCropRect.left - imageRectInViewport.left) / imageRectInViewport.width) * bitmapWidth)
        .roundToInt()
        .coerceIn(0, bitmapWidth - 1)
    val top = (((safeCropRect.top - imageRectInViewport.top) / imageRectInViewport.height) * bitmapHeight)
        .roundToInt()
        .coerceIn(0, bitmapHeight - 1)
    val right = (((safeCropRect.right - imageRectInViewport.left) / imageRectInViewport.width) * bitmapWidth)
        .roundToInt()
        .coerceIn(left + 1, bitmapWidth)
    val bottom = (((safeCropRect.bottom - imageRectInViewport.top) / imageRectInViewport.height) * bitmapHeight)
        .roundToInt()
        .coerceIn(top + 1, bitmapHeight)

    return BitmapRect(left, top, right, bottom)
}

internal fun calculateFittedImageRect(
    containerSize: Size,
    bitmapWidth: Int,
    bitmapHeight: Int
): Rect {
    if (containerSize.width <= 0f || containerSize.height <= 0f || bitmapWidth <= 0 || bitmapHeight <= 0) {
        return Rect(0f, 0f, 1f, 1f)
    }

    val scale = minOf(
        containerSize.width / bitmapWidth.toFloat(),
        containerSize.height / bitmapHeight.toFloat()
    )
    val imageWidth = bitmapWidth * scale
    val imageHeight = bitmapHeight * scale
    val left = (containerSize.width - imageWidth) / 2f
    val top = (containerSize.height - imageHeight) / 2f

    return Rect(
        left = left,
        top = top,
        right = left + imageWidth,
        bottom = top + imageHeight
    )
}

internal fun Offset.coercePan(
    imageRect: Rect,
    scale: Float,
    containerSize: Size
): Offset {
    val scaledRect = imageRect.scaleFromCenter(scale)
    val minX = if (scaledRect.width > containerSize.width) {
        containerSize.width - scaledRect.right
    } else {
        imageRect.center.x - scaledRect.center.x
    }
    val maxX = if (scaledRect.width > containerSize.width) {
        -scaledRect.left
    } else {
        imageRect.center.x - scaledRect.center.x
    }
    val minY = if (scaledRect.height > containerSize.height) {
        containerSize.height - scaledRect.bottom
    } else {
        imageRect.center.y - scaledRect.center.y
    }
    val maxY = if (scaledRect.height > containerSize.height) {
        -scaledRect.top
    } else {
        imageRect.center.y - scaledRect.center.y
    }

    return Offset(
        x = x.coerceIn(minX, maxX),
        y = y.coerceIn(minY, maxY)
    )
}

internal fun Rect.scaleFromCenter(scale: Float): Rect {
    val center = this.center
    val newWidth = width * scale
    val newHeight = height * scale

    return Rect(
        left = center.x - newWidth / 2f,
        top = center.y - newHeight / 2f,
        right = center.x + newWidth / 2f,
        bottom = center.y + newHeight / 2f
    )
}

internal fun Rect.translateBy(offset: Offset): Rect {
    return Rect(
        left = left + offset.x,
        top = top + offset.y,
        right = right + offset.x,
        bottom = bottom + offset.y
    )
}

internal fun Rect.handleOffset(position: CropHandlePosition): Offset {
    return when (position) {
        CropHandlePosition.TopLeft -> Offset(left, top)
        CropHandlePosition.TopRight -> Offset(right, top)
        CropHandlePosition.BottomLeft -> Offset(left, bottom)
        CropHandlePosition.BottomRight -> Offset(right, bottom)
    }
}

private fun createCropGeometry(
    cropRect: CropRect,
    imageRect: Rect,
    viewportRect: Rect,
    minCropSizePx: Float
): CropGeometry {
    val cropBounds = imageRect.intersectWith(viewportRect)
    val minCropWidthPx = minCropSizePx.coerceAtMost(cropBounds.width)
    val minCropHeightPx = minCropSizePx.coerceAtMost(cropBounds.height)
    val cropRectOnScreen = cropRect
        .toScreenRect(imageRect)
        .constrainTo(
            bounds = cropBounds,
            minWidth = minCropWidthPx,
            minHeight = minCropHeightPx
        )

    return CropGeometry(
        imageRect = imageRect,
        viewportRect = viewportRect,
        cropBounds = cropBounds,
        cropRectOnScreen = cropRectOnScreen,
        minCropWidthPx = minCropWidthPx,
        minCropHeightPx = minCropHeightPx
    )
}

private fun CropRect.toScreenRect(imageRect: Rect): Rect {
    return Rect(
        left = imageRect.left + imageRect.width * left,
        top = imageRect.top + imageRect.height * top,
        right = imageRect.left + imageRect.width * right,
        bottom = imageRect.top + imageRect.height * bottom
    )
}

private fun Rect.toCropRect(imageRect: Rect): CropRect {
    return CropRect(
        left = ((left - imageRect.left) / imageRect.width).coerceIn(0f, 1f),
        top = ((top - imageRect.top) / imageRect.height).coerceIn(0f, 1f),
        right = ((right - imageRect.left) / imageRect.width).coerceIn(0f, 1f),
        bottom = ((bottom - imageRect.top) / imageRect.height).coerceIn(0f, 1f)
    ).normalized()
}

private fun Rect.resize(
    position: CropHandlePosition,
    drag: Offset,
    bounds: Rect,
    minWidth: Float,
    minHeight: Float
): Rect {
    var newLeft = left
    var newTop = top
    var newRight = right
    var newBottom = bottom

    when (position) {
        CropHandlePosition.TopLeft -> {
            newLeft = (left + drag.x).coerceIn(bounds.left, right - minWidth)
            newTop = (top + drag.y).coerceIn(bounds.top, bottom - minHeight)
        }
        CropHandlePosition.TopRight -> {
            newRight = (right + drag.x).coerceIn(left + minWidth, bounds.right)
            newTop = (top + drag.y).coerceIn(bounds.top, bottom - minHeight)
        }
        CropHandlePosition.BottomLeft -> {
            newLeft = (left + drag.x).coerceIn(bounds.left, right - minWidth)
            newBottom = (bottom + drag.y).coerceIn(top + minHeight, bounds.bottom)
        }
        CropHandlePosition.BottomRight -> {
            newRight = (right + drag.x).coerceIn(left + minWidth, bounds.right)
            newBottom = (bottom + drag.y).coerceIn(top + minHeight, bounds.bottom)
        }
    }

    return Rect(newLeft, newTop, newRight, newBottom)
}

private fun Rect.constrainTo(
    bounds: Rect,
    minWidth: Float,
    minHeight: Float
): Rect {
    val targetWidth = width.coerceAtLeast(minWidth).coerceAtMost(bounds.width)
    val targetHeight = height.coerceAtLeast(minHeight).coerceAtMost(bounds.height)
    val targetLeft = left.coerceIn(bounds.left, bounds.right - targetWidth)
    val targetTop = top.coerceIn(bounds.top, bounds.bottom - targetHeight)

    return Rect(
        left = targetLeft,
        top = targetTop,
        right = targetLeft + targetWidth,
        bottom = targetTop + targetHeight
    )
}

private fun CropRect.normalized(): CropRect {
    val safeLeft = left.coerceIn(0f, 1f)
    val safeTop = top.coerceIn(0f, 1f)
    val safeRight = right.coerceIn(safeLeft, 1f)
    val safeBottom = bottom.coerceIn(safeTop, 1f)

    return CropRect(
        left = safeLeft,
        top = safeTop,
        right = safeRight,
        bottom = safeBottom
    )
}

private fun Rect.intersectWith(other: Rect): Rect {
    val newLeft = maxOf(left, other.left)
    val newTop = maxOf(top, other.top)
    val newRight = minOf(right, other.right)
    val newBottom = minOf(bottom, other.bottom)

    return if (newRight > newLeft && newBottom > newTop) {
        Rect(newLeft, newTop, newRight, newBottom)
    } else {
        Rect(other.left, other.top, other.right, other.bottom)
    }
}
