package com.example.scanlink.features.document_scanner.presentation.preview.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PreviewBottomActions(
    cropMode: Boolean,
    onRetake: () -> Unit,
    onRotate: () -> Unit,
    onCrop: () -> Unit,
    onExtractText: () -> Unit,
    onDone: () -> Unit,
    onApplyCrop: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF202124))
            .navigationBarsPadding()
    ) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PreviewActionItem(Icons.Default.DocumentScanner, "Retake", onClick = onRetake)
            PreviewActionItem(Icons.Default.RotateRight, "Rotate", onClick = onRotate)
            PreviewActionItem(
                icon = Icons.Default.Crop,
                label = "Crop",
                tint = if (cropMode)
                    Color(0xFF63DDB4)
                else
                    Color(0xFFEDEDED),
                onClick = onCrop
            )
            PreviewActionItem(Icons.Default.TextFields, "Extract Text", onClick = onExtractText)

            IconButton(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF63DDB4)),
                onClick = {
                    if (cropMode) {
                        onApplyCrop()
                    } else {
                        onDone()
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Done",
                    tint = Color(0xFF0F172A)
                )
            }
        }
    }
}

@Composable
private fun PreviewActionItem(
    icon: ImageVector,
    label: String,
    tint: Color = Color.White,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onClick) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFFEDEDED)
            )
        }
        Text(
            text = label,
            color = tint,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}
