package com.example.scanlink.features.document_scanner.presentation.camera.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.features.document_scanner.presentation.camera.CameraUiState

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ScanningOverlay(
    modifier: Modifier = Modifier,
    bitmap: Bitmap?,
    state: CameraUiState
) {
    if (bitmap == null) return

    val infiniteTransition = rememberInfiniteTransition(label = "scanning")
    val translateY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteTransitionSpec(),
        label = "lineAnimation"
    )

    val statusText = when (state) {
        is CameraUiState.Transforming -> "Đang căn chỉnh tài liệu..."
        is CameraUiState.Filtering -> "Đang tối ưu màu sắc..."
        is CameraUiState.OcrProcessing -> "Đang nhận diện chữ viết..."
        else -> "Đang xử lý..."
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // 1. Hiển thị ảnh đang xử lý (Cắt phẳng hoặc Đen trắng)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        // 2. Hiệu ứng đường quét (Laser line)
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val scanLineY = maxHeight * translateY

            // Đường quét màu xanh neon
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .offset(y = scanLineY)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary,
                                Color.Transparent
                            )
                        )
                    )
            )

            // Hiệu ứng phát sáng mờ phía sau đường quét
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .offset(y = scanLineY - 20.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }

        // 3. Text trạng thái ở phía dưới
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
                .background(Color.Black.copy(alpha = 0.6f), shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = statusText,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun infiniteTransitionSpec() = infiniteRepeatable<Float>(
    animation = tween(2000, easing = LinearEasing),
    repeatMode = RepeatMode.Reverse
)
