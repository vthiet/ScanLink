package com.example.scanlink.features.document_scanner.presentation.ocr

import android.graphics.Bitmap
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.scanlink.features.document_scanner.presentation.ocr.components.*
import kotlin.math.roundToInt

@Composable
fun OcrResultContent(
    processedBitmap: Bitmap?,
    detectedText: String,
    pdfPath: String?,
    onBackClick: () -> Unit,
    onSaveToDbClick: () -> Unit
) {
    var showFullScreenImage by remember { mutableStateOf(false) }
    
    // Trạng thái kéo: 0 là bình thường, số âm là kéo lên (ẩn ảnh)
    var offsetY by remember { mutableStateOf(0f) }
    val density = LocalDensity.current
    
    // Giới hạn kéo lên tối đa là 200dp (đủ che phần ảnh)
    val maxCollapseOffset = with(density) { -200.dp.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
    ) {
        // 1. LỚP DƯỚI: Chứa phần ảnh (Sẽ bị che khi kéo phần Text lên)
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
                    colors = CardDefaults.cardColors(containerColor = Color.Black)
                ) {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Tap to zoom",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
            
            DocumentInfoCard(pdfPath = pdfPath)
        }

        // 2. LỚP TRÊN: Phần Text và Actions (Có thể kéo lên xuống)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(0, (280.dp.toPx() + offsetY).roundToInt()) } // Vị trí bắt đầu dưới phần ảnh
                .background(
                    color = Color(0xFF111111),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState { delta ->
                        val newOffset = offsetY + delta
                        offsetY = newOffset.coerceIn(maxCollapseOffset, 0f)
                    }
                )
        ) {
            // Thanh cầm để kéo (Drag Handle)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.DragHandle,
                    contentDescription = null,
                    tint = Color.Gray,
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
                isSaveEnabled = pdfPath != null
            )
            
            // Padding dưới cùng để tránh bị đè bởi Bottom Bar nếu có
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
                .background(Color.Black)
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
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
        }
    }
}
