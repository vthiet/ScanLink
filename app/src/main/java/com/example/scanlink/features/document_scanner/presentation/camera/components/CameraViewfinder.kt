package com.example.scanlink.features.document_scanner.presentation.camera.components

import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay

@Composable
fun CameraViewfinder(
    onImageCaptureReady: (ImageCapture) -> Unit = {},
    isFrontCamera: Boolean = false,
    flashEnabled: Boolean = false
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var selectedZoom by remember { mutableStateOf(1f) }
    var zoomOptions by remember { mutableStateOf(listOf(1f)) }
    var showZoomControls by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val tapInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(showZoomControls, selectedZoom) {
        if (showZoomControls) {
            delay(2500)
            showZoomControls = false
        }
    }

    LaunchedEffect(camera, flashEnabled) {
        camera?.applyTorch(flashEnabled)
        imageCapture?.flashMode = ImageCapture.FLASH_MODE_OFF
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }
            },
            update = { previewView ->
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val newImageCapture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .setFlashMode(ImageCapture.FLASH_MODE_OFF)
                        .build()

                    val cameraSelector = if (isFrontCamera) {
                        CameraSelector.DEFAULT_FRONT_CAMERA
                    } else {
                        CameraSelector.DEFAULT_BACK_CAMERA
                    }

                    try {
                        cameraProvider.unbindAll()
                        camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            newImageCapture
                        )
                        imageCapture = newImageCapture
                        zoomOptions = camera?.availableScanZoomRatios().orEmpty().ifEmpty { listOf(1f) }
                        selectedZoom = selectedZoom.coerceToNearest(zoomOptions)
                        camera?.setSafeZoomRatio(selectedZoom)
                        camera?.applyTorch(flashEnabled)
                        onImageCaptureReady(newImageCapture)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }, ContextCompat.getMainExecutor(context))
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = tapInteractionSource,
                    indication = null
                ) {
                    if (zoomOptions.size > 1) {
                        showZoomControls = true
                    }
                }
        )

        ScanAnimation()

        if (showZoomControls && zoomOptions.size > 1) {
            ZoomStrip(
                zoomOptions = zoomOptions,
                selectedZoom = selectedZoom,
                onZoomSelected = { ratio ->
                    selectedZoom = ratio
                    showZoomControls = true
                    camera?.setSafeZoomRatio(ratio)
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun ZoomStrip(
    zoomOptions: List<Float>,
    selectedZoom: Float,
    onZoomSelected: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(bottom = 18.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        zoomOptions.forEach { zoom ->
            val selected = selectedZoom == zoom
            Text(
                text = "${zoom.formatZoom()}x",
                modifier = Modifier
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        if (selected) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        } else {
                            androidx.compose.ui.graphics.Color.Transparent
                        }
                    )
                    .clickable { onZoomSelected(zoom) }
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun Float.formatZoom(): String {
    return if (this % 1f == 0f) this.toInt().toString() else toString()
}

private fun Camera.availableScanZoomRatios(): List<Float> {
    val zoomState = cameraInfo.zoomState.value ?: return listOf(1f)
    val minZoom = zoomState.minZoomRatio
    val maxZoom = zoomState.maxZoomRatio.coerceAtMost(2f)
    return listOf(0.5f, 1f, 1.5f, 2f)
        .filter { it in minZoom..maxZoom }
        .ifEmpty { listOf(1f.coerceIn(minZoom, maxZoom)) }
}

private fun Float.coerceToNearest(options: List<Float>): Float {
    return options.minByOrNull { kotlin.math.abs(it - this) } ?: this
}

private fun Camera.setSafeZoomRatio(ratio: Float) {
    val zoomState = cameraInfo.zoomState.value
    val safeRatio = if (zoomState != null) {
        ratio.coerceIn(zoomState.minZoomRatio, zoomState.maxZoomRatio)
    } else {
        ratio
    }
    cameraControl.setZoomRatio(safeRatio)
}

private fun Camera.applyTorch(enabled: Boolean) {
    if (cameraInfo.hasFlashUnit()) {
        cameraControl.enableTorch(enabled)
    }
}
