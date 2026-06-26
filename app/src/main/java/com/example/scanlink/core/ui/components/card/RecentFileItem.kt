package com.example.scanlink.core.ui.components.card

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.scanlink.core.ui.model.FileType
import com.example.scanlink.core.ui.model.RecentFile
import com.example.scanlink.core.ui.components.button.CircleActionButton

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
                .background(MaterialTheme.colorScheme.surface)
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
                    color = MaterialTheme.colorScheme.onSurface,
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
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )

                    file.sizeLabel?.let {

                        Text(
                            text = " • $it",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
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
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
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
                color = MaterialTheme.colorScheme.surfaceVariant
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