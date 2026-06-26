package com.example.scanlink.features.document_scanner.presentation.ocr

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.scanlink.features.document_scanner.presentation.camera.ScanFilterType
import com.example.scanlink.features.document_scanner.presentation.ocr.components.*
import kotlin.math.roundToInt

@Composable
fun OcrResultContent(
    processedBitmap: Bitmap?,
    detectedText: String,
    pdfPath: String?,
    selectedFilter: ScanFilterType,
    onFilterSelected: (ScanFilterType) -> Unit,
    onBackClick: () -> Unit,
    onSaveToDbClick: () -> Unit
) {
    var showFullScreenImage by remember { mutableStateOf(false) }
    
    // Trạng thái kéo: 0 là bình thường, số âm là kéo lên (ẩn ảnh)
    var offsetY by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    
    // Tăng giới hạn kéo lên để phủ hết phần ảnh (320dp là điểm bắt đầu)
    val maxCollapseOffset = with(density) { -320.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. LỚP DƯỚI: Chứa phần ảnh và bộ lọc
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            OcrTopBar(onBackClick = onBackClick)

            processedBitmap?.let {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { showFullScreenImage = true },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                ) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Tap to zoom",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            // Bộ lọc màu
            FilterSelector(
                selectedFilter = selectedFilter,
                onFilterSelected = onFilterSelected
            )
            
            DocumentInfoCard(pdfPath = pdfPath)
        }

        // 2. LỚP TRÊN: Phần Text và Actions
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, (320.dp.toPx() + offsetY).roundToInt()) }
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        val newOffset = offsetY + delta
                        // Cho phép kéo lên hết cỡ để che ảnh, và kéo xuống một chút tạo hiệu ứng bounce
                        offsetY = newOffset.coerceIn(maxCollapseOffset, 50f)
                    }
                )
        ) {
            // Thanh cầm để kéo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }

            OcrActionRow(
                textToCopy = detectedText,
                pdfPath = pdfPath
            )

            OcrTextCard(
                text = detectedText,
                modifier = Modifier.weight(1f)
            )

            OcrBottomActions(
                onCancelClick = onBackClick,
                onSaveClick = onSaveToDbClick,
                isSaveEnabled = true
            )
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showFullScreenImage && processedBitmap != null) {
        FullScreenImageDialog(
            bitmap = processedBitmap,
            onDismiss = { showFullScreenImage = false }
        )
    }
}

@Composable
fun FullScreenImageDialog(bitmap: Bitmap, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        var scale by remember { mutableStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        
        val state = rememberTransformableState { zoomChange, offsetChange, _ ->
            scale = (scale * zoomChange).coerceIn(1f, 5f)
            offset += offsetChange
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .transformable(state = state),
                contentScale = ContentScale.Fit
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.75f), CircleShape)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
