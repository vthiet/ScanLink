package com.example.scanlink.features.document_scanner.presentation.preview.components

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
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
    var viewportSizePx by remember { mutableStateOf(IntSize.Zero) }
    var zoom by remember { mutableStateOf(1f) }
    var pan by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { viewportSizePx = it }
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, shape)
    ) {
        LaunchedEffect(previewBitmap) {
            zoom = 1f
            pan = Offset.Zero
        }

        val viewportSize = Size(
            width = viewportSizePx.width.toFloat().coerceAtLeast(1f),
            height = viewportSizePx.height.toFloat().coerceAtLeast(1f)
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(viewportSize) {
                    detectTransformGestures { _, panChange, zoomChange, _ ->
                        zoom = (zoom * zoomChange).coerceIn(1f, 5f)
                        pan += panChange
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (previewBitmap != null) {
                Image(
                    bitmap = previewBitmap.asImageBitmap(),
                    contentDescription = "Document preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            translationX = pan.x,
                            translationY = pan.y,
                            scaleX = zoom * if (flipHorizontal) -1f else 1f,
                            scaleY = zoom * if (flipVertical) -1f else 1f,
                            rotationZ = rotation
                        ),
                    contentScale = ContentScale.Fit
                )
            } else {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Document preview",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = if (flipHorizontal) -1f else 1f,
                            scaleY = if (flipVertical) -1f else 1f,
                            rotationZ = rotation
                        ),
                    contentScale = ContentScale.Fit
                )
            }
        }

        if (cropMode && previewBitmap != null) {
            CropOverlay(
                cropRect = cropRect,
                onCropRectChange = onCropRectChange,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun CropOverlay(
    cropRect: CropRect,
    onCropRectChange: (CropRect) -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val left = size.width * cropRect.left
        val top = size.height * cropRect.top
        val right = size.width * cropRect.right
        val bottom = size.height * cropRect.bottom

        // Draw dim background
        drawRect(color = Color.Black.copy(alpha = 0.5f))
        
        // Draw clear hole for crop area (simplified)
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top)
        )

        // Draw border
        drawRect(
            color = Color(0xFF5EEAD4),
            topLeft = Offset(left, top),
            size = Size(right - left, bottom - top),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}
