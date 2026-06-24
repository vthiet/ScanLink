package com.example.scanlink.features.document_scanner.presentation.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.document_scanner.data.engine.ScanEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val scanEngine: ScanEngine
): ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiStateHolder())
    val uiState = _uiState.asStateFlow()

    fun onModeSelected(mode: String) {
        _uiState.value = _uiState.value.copy(selectedMode = mode)
    }

    fun toggleFlash() {
        _uiState.value = _uiState.value.copy(flashEnabled = !_uiState.value.flashEnabled)
    }

    fun switchCamera() {
        _uiState.value = _uiState.value.copy(isFrontCamera = !_uiState.value.isFrontCamera)
    }

    fun onCaptureStarted() {
        _uiState.update {
            it.copy(
                uiState = CameraUiState.Capturing,
                isLoading = true
            )
        }
    }

    fun onCaptureSuccess(context: Context, imageUri: String) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, uiState = CameraUiState.Transforming) }
                
                val uri = Uri.parse(imageUri)
                
                // 1. Load và Xoay ảnh đúng chiều dựa trên EXIF
                val originalBitmap = withContext(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    
                    if (bitmap != null) {
                        rotateImageIfRequired(context, bitmap, uri)
                    } else null
                }

                if (originalBitmap == null) {
                    onCaptureError("Không thể đọc dữ liệu ảnh chụp.")
                    return@launch
                }

                _uiState.update { it.copy(originalBitmap = originalBitmap) }

                // 2. Perspective Transform (Cắt phẳng)
                val (transformed, detected) = withContext(Dispatchers.Default) {
                    scanEngine.transformDocument(originalBitmap)
                }
                
                _uiState.update { 
                    it.copy(
                        processedBitmap = transformed, 
                        uiState = CameraUiState.Filtering 
                    ) 
                }

                // 3. Apply Filters
                kotlinx.coroutines.delay(300) 
                val filtered = withContext(Dispatchers.Default) {
                    scanEngine.applyFilters(transformed)
                }

                _uiState.update { 
                    it.copy(
                        processedBitmap = filtered,
                        uiState = CameraUiState.OcrProcessing
                    ) 
                }

                // 4. OCR & PDF
                val textTask = viewModelScope.launch(Dispatchers.Default) {
                    val text = scanEngine.extractText(transformed)
                    _uiState.update { it.copy(detectedText = text.ifBlank { "Không tìm thấy nội dung chữ." }) }
                }

                val pdfFile = withContext(Dispatchers.IO) {
                    scanEngine.createPdf(filtered, "Scan_${System.currentTimeMillis()}")
                }

                textTask.join()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        uiState = CameraUiState.Success(imageUri, it.selectedMode),
                        pdfPath = pdfFile?.absolutePath
                    )
                }

            } catch (e: Exception) {
                onCaptureError("Lỗi xử lý tài liệu: ${e.localizedMessage}")
            }
        }
    }

    private fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap {
        val input = context.contentResolver.openInputStream(selectedImage)
        val ei = input?.use { ExifInterface(it) } ?: return img
        
        val orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

        return when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(img, 90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(img, 180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(img, 270f)
            else -> img
        }
    }

    private fun rotateImage(img: Bitmap, degree: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree)
        val rotatedImg = Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
        return rotatedImg
    }

    fun onCaptureError(message: String) {
        _uiState.value = _uiState.value.copy(
            uiState = CameraUiState.Error(message),
            isLoading = false
        )
    }
}
