package com.example.scanlink.features.file_sharing.presentation.gallery.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.scanlink.features.file_sharing.presentation.model.DocumentUiModel

@Composable
fun DocumentItem(
    document: DocumentUiModel
) {

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector =
                    Icons.Default.Description,
                contentDescription = null
            )

            Spacer(
                modifier =
                    Modifier.width(12.dp)
            )

            Column(
                modifier =
                    Modifier.weight(1f)
            ) {

                Text(
                    text = document.title,
                    style =
                        MaterialTheme
                            .typography
                            .titleMedium
                )

                Text(
                    text = document.fileSize,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )
            }

            when (document.uploadStatus) {

                "Uploaded" -> {

                    Icon(
                        imageVector =
                            Icons.Default.CheckCircle,
                        contentDescription = null
                    )
                }

                "Pending" -> {

                    Icon(
                        imageVector =
                            Icons.Default.Schedule,
                        contentDescription = null
                    )
                }

                else -> {

                    Icon(
                        imageVector =
                            Icons.Default.Sync,
                        contentDescription = null
                    )
                }
            }
        }
    }
}