package com.example.scanlink.features.document_scanner.presentation.preview.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.scanlink.features.document_scanner.domain.entities.CropRect

@Composable
fun PreviewImageViewer(
    previewBitmap: Bitmap?,
    imageUri: String,
    rotation: Float,
    flipHorizontal: Boolean,
    flipVertical: Boolean,
    cropMode: Boolean,
    cropRect: CropRect,
    onCropRectChange: (CropRect) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val shape = RoundedCornerShape(14.dp)
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.outlineVariant
    var viewportSizePx by remember { mutableStateOf(IntSize.Zero) }
    var zoom by remember { mutableStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { viewportSizePx = it }
            .clip(shape)
            .background(backgroundColor)
            .border(1.dp, borderColor, shape)
    ) {
        LaunchedEffect(previewBitmap) {
            zoom = 1f
            pan = Offset.Zero
        }

        val viewportSize = Size(
            width = viewportSizePx.width.toFloat().coerceAtLeast(1f),
            height = viewportSizePx.height.toFloat().coerceAtLeast(1f)
        )
        val bitmapWidth = previewBitmap?.width ?: 1
        val bitmapHeight = previewBitmap?.height ?: 1
        val fittedImageRect = remember(viewportSize, bitmapWidth, bitmapHeight) {
            calculateFittedImageRect(
                containerSize = viewportSize,
                bitmapWidth = bitmapWidth,
                bitmapHeight = bitmapHeight
            )
        }
        val transformedImageRect = fittedImageRect
            .scaleFromCenter(zoom)
            .translateBy(pan)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(fittedImageRect, viewportSize) {
                    detectTransformGestures { _, panChange, zoomChange, _ ->
                        val nextZoom = (zoom * zoomChange).coerceIn(MinZoom, MaxZoom)
                        zoom = nextZoom
                        pan = (pan + panChange).coercePan(
                            imageRect = fittedImageRect,
                            scale = nextZoom,
                            containerSize = viewportSize
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (previewBitmap != null) {
                Image(
                    bitmap = previewBitmap.asImageBitmap(),
                    contentDescription = "Document preview",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(
                            width = with(density) { fittedImageRect.width.toDp() },
                            height = with(density) { fittedImageRect.height.toDp() }
                        )
                        .graphicsLayer(
                            translationX = pan.x,
                            translationY = pan.y,
                            scaleX = zoom * if (flipHorizontal) -1f else 1f,
                            scaleY = zoom * if (flipVertical) -1f else 1f,
                            rotationZ = rotation
                        )
                )
            } else {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Document preview",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = if (flipHorizontal) -1f else 1f,
                            scaleY = if (flipVertical) -1f else 1f,
                            rotationZ = rotation
                        )
                )
            }
        }

        if (cropMode && previewBitmap != null) {
            CropOverlay(
                cropRect = cropRect,
                geometry = rememberCropGeometry(
                    cropRect = cropRect,
                    imageRect = transformedImageRect,
                    viewportRect = Rect(0f, 0f, viewportSize.width, viewportSize.height),
                    minCropSizePx = with(density) { MinCropSize.toPx() }
                ),
                onCropRectChange = onCropRectChange,
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(1f)
            )
        }
    }
}

private const val MinZoom = 1f
private const val MaxZoom = 5f
private val MinCropSize = 80.dp
