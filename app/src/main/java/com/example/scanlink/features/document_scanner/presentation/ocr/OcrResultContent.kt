package com.example.scanlink.features.document_scanner.presentation.ocr

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.scanlink.features.document_scanner.presentation.ocr.components.*

@Composable
fun OcrResultContent(
    processedBitmap: Bitmap?,
    detectedText: String,
    pdfPath: String?,
    onBackClick: () -> Unit,
    onSaveToDbClick: () -> Unit
) {
    var showFullScreenImage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
    ) {
        OcrTopBar(
            onBackClick = onBackClick
        )

        // Hiển thị ảnh xem trước (Nhấn vào để phóng to)
        processedBitmap?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Giảm chiều cao xuống để ưu tiên phần Text
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

        // Các thông tin phụ thu nhỏ lại một chút
        DocumentInfoCard(pdfPath = pdfPath)

        OcrActionRow(
            textToCopy = detectedText,
            pdfPath = pdfPath
        )

        // Phần văn bản chiếm phần lớn diện tích còn lại
        OcrTextCard(
            text = detectedText,
            modifier = Modifier.weight(1f)
        )

        OcrBottomActions(
            onCancelClick = onBackClick,
            onSaveClick = onSaveToDbClick,
            isSaveEnabled = pdfPath != null
        )
    }

    // Dialog hiển thị ảnh Full Screen có Zoom
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
        
        // Sử dụng phiên bản mới của rememberTransformableState để tránh warning
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

            // Nút đóng
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
            }
            
            Text(
                text = "Dùng 2 ngón tay để phóng to",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
            )
        }
    }
}
