package com.example.scanlink.features.document_scanner.presentation.preview.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.scanlink.features.document_scanner.presentation.preview.CropRect
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@Composable
fun PreviewImageViewer(
    imageUri: String,
    rotation: Float,
    flipHorizontal: Boolean,
    flipVertical: Boolean,
    cropMode: Boolean,
    cropRect: CropRect,
    onCropRectChange: (CropRect) -> Unit,
    modifier: Modifier = Modifier
){
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF111827))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.10f),
                shape = RoundedCornerShape(14.dp)
            )
    ) {
        AsyncImage(
            model = imageUri,
            contentDescription = "Document preview",
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .graphicsLayer(
                    rotationZ = rotation,
                    scaleX = if (flipHorizontal) -1f else 1f,
                    scaleY = if (flipVertical) -1f else 1f
                ),
            contentScale = ContentScale.Fit
        )

        if (cropMode) {
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
    Box(modifier = modifier) {

        Canvas(modifier = Modifier.fillMaxSize()) {

            val left = size.width * cropRect.left
            val top = size.height * cropRect.top
            val right = size.width * cropRect.right
            val bottom = size.height * cropRect.bottom

            drawRect(
                color = Color.Black.copy(alpha = 0.45f)
            )

            drawRoundRect(
                color = Color(0xFF5EEAD4),
                topLeft = Offset(left, top),
                size = Size(
                    right - left,
                    bottom - top
                ),
                style = Stroke(width = 4f)
            )
        }

        CropHandle(
            cropRect.left,
            cropRect.top
        ) { dx, dy ->
            onCropRectChange(
                cropRect.copy(
                    left = (cropRect.left + dx).coerceIn(
                        0f,
                        cropRect.right - 0.1f
                    ),
                    top = (cropRect.top + dy).coerceIn(
                        0f,
                        cropRect.bottom - 0.1f
                    )
                )
            )
        }

        CropHandle(
            cropRect.right,
            cropRect.bottom
        ) { dx, dy ->
            onCropRectChange(
                cropRect.copy(
                    right = (cropRect.right + dx).coerceIn(
                        cropRect.left + 0.1f,
                        1f
                    ),
                    bottom = (cropRect.bottom + dy).coerceIn(
                        cropRect.top + 0.1f,
                        1f
                    )
                )
            )
        }
    }
}
@Composable
private fun CropHandle(
    xPercent: Float,
    yPercent: Float,
    onDrag: (Float, Float) -> Unit
) {
    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    (xPercent * 800).roundToInt(),
                    (yPercent * 1200).roundToInt()
                )
            }
            .background(Color(0xFF5EEAD4))
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    onDrag(
                        dragAmount.x / 800f,
                        dragAmount.y / 1200f
                    )
                }
            }
    )
}
