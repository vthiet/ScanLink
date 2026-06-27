package com.example.scanlink.features.document_scanner.presentation.preview.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.scanlink.features.document_scanner.domain.entities.CropRect
import kotlin.math.roundToInt

@Composable
fun CropOverlay(
    cropRect: CropRect,
    geometry: CropGeometry,
    onCropRectChange: (CropRect) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val overlayColor = MaterialTheme.colorScheme.scrim.copy(alpha = 0.56f)
    val cropColor = MaterialTheme.colorScheme.primary
    val handleColor = MaterialTheme.colorScheme.primary
    val handleBorderColor = MaterialTheme.colorScheme.onPrimary
    val handleSize = 26.dp
    val cropRectOnScreen = geometry.cropRectOnScreen

    val currentGeometry by rememberUpdatedState(geometry)
    val currentOnCropRectChange by rememberUpdatedState(onCropRectChange)
    var moveStartRect by remember { mutableStateOf(cropRectOnScreen) }
    var handleStartRect by remember { mutableStateOf(cropRectOnScreen) }

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCropScrim(
                overlayColor = overlayColor,
                cropRect = cropRectOnScreen,
                viewportSize = size
            )

            drawRect(
                color = cropColor,
                topLeft = Offset(cropRectOnScreen.left, cropRectOnScreen.top),
                size = Size(cropRectOnScreen.width, cropRectOnScreen.height),
                style = Stroke(width = 3.dp.toPx())
            )

            drawRuleOfThirds(
                cropRect = cropRectOnScreen,
                color = cropColor.copy(alpha = 0.45f),
                strokeWidth = 1.dp.toPx()
            )
        }

        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        cropRectOnScreen.left.roundToInt(),
                        cropRectOnScreen.top.roundToInt()
                    )
                }
                .size(
                    width = with(density) { cropRectOnScreen.width.toDp() },
                    height = with(density) { cropRectOnScreen.height.toDp() }
                )
                .background(Color.Transparent)
                .zIndex(2f)
                .pointerInput(Unit) {
                    var totalDrag = Offset.Zero
                    detectDragGestures(
                        onDragStart = {
                            totalDrag = Offset.Zero
                            moveStartRect = currentGeometry.cropRectOnScreen
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            totalDrag += dragAmount
                            currentOnCropRectChange(
                                currentGeometry.moveCropRect(moveStartRect, totalDrag)
                            )
                        }
                    )
                }
        )

        CropHandlePosition.entries.forEach { position ->
            val handleOffset = cropRectOnScreen.handleOffset(position)
            CropHandle(
                x = handleOffset.x,
                y = handleOffset.y,
                size = handleSize,
                color = handleColor,
                borderColor = handleBorderColor,
                onDragStart = {
                    handleStartRect = currentGeometry.cropRectOnScreen
                },
                onDrag = { drag ->
                    currentOnCropRectChange(
                        currentGeometry.resizeCropRect(
                            startRect = handleStartRect,
                            position = position,
                            drag = drag
                        )
                    )
                }
            )
        }
    }
}

@Composable
fun CropHandle(
    x: Float,
    y: Float,
    size: Dp,
    color: Color,
    borderColor: Color,
    onDragStart: () -> Unit,
    onDrag: (Offset) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val touchSize = if (size < MinTouchTargetSize) MinTouchTargetSize else size
    val halfTouchSizePx = with(density) { touchSize.toPx() / 2f }

    Box(
        modifier = modifier
            .offset {
                IntOffset(
                    x = (x - halfTouchSizePx).roundToInt(),
                    y = (y - halfTouchSizePx).roundToInt()
                )
            }
            .size(touchSize)
            .zIndex(3f)
            .pointerInput(Unit) {
                var totalDrag = Offset.Zero
                detectDragGestures(
                    onDragStart = {
                        totalDrag = Offset.Zero
                        onDragStart()
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        totalDrag += dragAmount
                        onDrag(totalDrag)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(7.dp))
                .background(color)
                .border(2.dp, borderColor, RoundedCornerShape(7.dp))
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCropScrim(
    overlayColor: Color,
    cropRect: Rect,
    viewportSize: Size
) {
    drawRect(
        color = overlayColor,
        topLeft = Offset.Zero,
        size = Size(viewportSize.width, cropRect.top.coerceAtLeast(0f))
    )
    drawRect(
        color = overlayColor,
        topLeft = Offset(0f, cropRect.bottom),
        size = Size(viewportSize.width, (viewportSize.height - cropRect.bottom).coerceAtLeast(0f))
    )
    drawRect(
        color = overlayColor,
        topLeft = Offset(0f, cropRect.top),
        size = Size(cropRect.left.coerceAtLeast(0f), cropRect.height)
    )
    drawRect(
        color = overlayColor,
        topLeft = Offset(cropRect.right, cropRect.top),
        size = Size((viewportSize.width - cropRect.right).coerceAtLeast(0f), cropRect.height)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawRuleOfThirds(
    cropRect: Rect,
    color: Color,
    strokeWidth: Float
) {
    val verticalStep = cropRect.width / 3f
    val horizontalStep = cropRect.height / 3f

    repeat(2) { index ->
        val x = cropRect.left + verticalStep * (index + 1)
        drawLine(
            color = color,
            start = Offset(x, cropRect.top),
            end = Offset(x, cropRect.bottom),
            strokeWidth = strokeWidth
        )

        val y = cropRect.top + horizontalStep * (index + 1)
        drawLine(
            color = color,
            start = Offset(cropRect.left, y),
            end = Offset(cropRect.right, y),
            strokeWidth = strokeWidth
        )
    }
}

private val MinTouchTargetSize = 44.dp
