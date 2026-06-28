package com.example.scanlink.features.document_scanner.presentation.file_detail.components

import android.net.Uri
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page
import java.io.File

@Composable
fun FilePreviewCard(
    document: Document,
    modifier: Modifier = Modifier
) {
    var isFullscreen by rememberSaveable { mutableStateOf(false) }
    var fullscreenPagePath by rememberSaveable { mutableStateOf<String?>(null) }
    val previewPages = remember(document) { document.previewPages() }
    val showGrid = previewPages.size > 1

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (showGrid) {
                        Modifier
                    } else {
                        Modifier.aspectRatio(if (document.isPdf) 0.78f else 1f)
                    }
                )
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            if (showGrid) {
                PageGridPreview(
                    pages = previewPages,
                    onPageClick = { pagePath ->
                        fullscreenPagePath = pagePath
                        isFullscreen = true
                    },
                    modifier = Modifier.padding(12.dp)
                )
            } else {
                ZoomablePreview(
                    document = document,
                    pagePath = previewPages.firstOrNull()?.imagePath,
                    modifier = Modifier.fillMaxSize(),
                    onFullscreen = {
                        fullscreenPagePath = previewPages.firstOrNull()?.imagePath
                        isFullscreen = true
                    }
                )
            }

            if (!showGrid) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PreviewBadge(text = if (document.isPdf) "PDF" else "IMAGE")
                    PreviewBadge(text = "${document.safePageCount} pages")
                }

                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.82f),
                            RoundedCornerShape(8.dp)
                        ),
                    onClick = { isFullscreen = true }
                ) {
                    Icon(Icons.Default.Fullscreen, contentDescription = "Full screen")
                }
            }
        }
    }

    if (isFullscreen) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                ZoomablePreview(
                    document = document,
                    pagePath = fullscreenPagePath ?: previewPages.firstOrNull()?.imagePath,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp),
                    onFullscreen = {}
                )
                TextButton(
                    modifier = Modifier.align(Alignment.TopEnd),
                    onClick = { isFullscreen = false }
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
private fun ZoomablePreview(
    document: Document,
    pagePath: String? = null,
    modifier: Modifier = Modifier,
    onFullscreen: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(1f, 4f)
        offset = if (scale > 1f) offset + panChange else Offset.Zero
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        scale = if (scale > 1f) 1f else 2.35f
                        offset = Offset.Zero
                    },
                    onTap = { onFullscreen() }
                )
            }
            .transformable(transformableState),
        contentAlignment = Alignment.Center
    ) {
        val model = pagePath ?: document.previewPath
        if (model != null) {
            AsyncImage(
                model = model.toPreviewModel(),
                contentDescription = document.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )
        } else {
            DocumentPlaceholder(
                document = document,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
            )
        }
    }
}

@Composable
private fun PageGridPreview(
    pages: List<Page>,
    onPageClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        pages.chunked(2).forEach { rowPages ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowPages.forEach { page ->
                    PagePreviewTile(
                        page = page,
                        onClick = { onPageClick(page.imagePath) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowPages.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun PagePreviewTile(
    page: Page,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(0.72f)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = page.imagePath.toPreviewModel(),
            contentDescription = "Page ${page.pageNumber}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        PreviewBadge(
            text = page.pageNumber.toString(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        )
    }
}

private fun String.toPreviewModel(): Any {
    return if (startsWith("content://") || startsWith("file://") || startsWith("android.resource://")) {
        Uri.parse(this)
    } else {
        Uri.fromFile(File(this))
    }
}

private fun Document.previewPages(): List<Page> {
    val validPages = pages
        .filter { it.imagePath.isNotBlank() }
        .sortedBy { it.pageNumber }
    if (validPages.isNotEmpty()) return validPages

    val fallbackPath = previewPath ?: return emptyList()
    return listOf(
        Page(
            id = "preview",
            documentId = id,
            pageNumber = 1,
            imagePath = fallbackPath,
            ocrText = null,
            createdAt = createdAt
        )
    )
}

@Composable
private fun DocumentPlaceholder(
    document: Document,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (document.isPdf) Icons.Default.PictureAsPdf else Icons.Default.Image,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(76.dp)
        )
        Spacer(Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = document.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                repeat(6) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(if (it % 3 == 0) 0.72f else 1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }
        }
    }
}

@Composable
private fun PreviewBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onPrimary,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}
