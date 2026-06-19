package com.example.scanlink.features.file_sharing.presentation.ui.component.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.features.file_sharing.presentation.model.FileType
import com.example.scanlink.features.file_sharing.presentation.model.RecentFile
import com.example.scanlink.features.file_sharing.presentation.ui.component.button.CircleActionButton

@Composable
fun RecentFileItem(
    file: RecentFile,
    modifier: Modifier = Modifier,

    showShareButton: Boolean = true,
    showMoreButton: Boolean = true,
    showProgress: Boolean = false,
    showDivider: Boolean = false,

    onShareClick: () -> Unit = {},
    onMoreClick: () -> Unit = {}
) {

    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFF1A1A22))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            FileIcon(type = file.type)

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = file.name,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    file.statusText?.let {

                        Text(
                            text = it,
                            color = file.statusColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = file.createdAt,
                        color = Color(0xFF8A8A8A),
                        fontSize = 12.sp
                    )

                    file.sizeLabel?.let {

                        Text(
                            text = " • $it",
                            color = Color(0xFF8A8A8A),
                            fontSize = 12.sp
                        )
                    }
                }

                if (showProgress && file.uploadProgress < 1f) {

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { file.uploadProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(50.dp)),
                        color = Color(0xFF00CFA4),
                        trackColor = Color(0xFF30303A)
                    )
                }
            }

            if (showShareButton) {

                CircleActionButton(
                    icon = Icons.Default.Share,
                    onClick = onShareClick
                )

                Spacer(modifier = Modifier.width(8.dp))
            }

            if (showMoreButton) {

                CircleActionButton(
                    icon = Icons.Default.MoreHoriz,
                    onClick = onMoreClick
                )
            }
        }

        if (showDivider) {

            Spacer(modifier = Modifier.height(12.dp))

            HorizontalDivider(
                color = Color(0xFF2B2B33)
            )
        }
    }
}

@Composable
private fun FileIcon(
    type: FileType
) {

    val backgroundColor = when (type) {

        FileType.PDF -> Color(0xFFFF6B2C)

        FileType.DOCX -> Color(0xFF00CFA4)

        FileType.JPG -> Color(0xFF7A5CFF)

        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            tint = backgroundColor,
            modifier = Modifier.size(28.dp)
        )
    }
}