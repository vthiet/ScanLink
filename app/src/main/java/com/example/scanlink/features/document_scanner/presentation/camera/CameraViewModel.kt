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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.File
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val scanEngine: ScanEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUiStateHolder())
    val uiState = _uiState.asStateFlow()

    fun onModeSelected(mode: String) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    fun toggleFlash() {
        _uiState.update { it.copy(flashEnabled = !it.flashEnabled) }
    }

    fun switchCamera() {
        _uiState.update { it.copy(isFrontCamera = !it.isFrontCamera) }
    }

    fun onFilterSelected(filterType: ScanFilterType) {
        val transformed = _uiState.value.transformedBitmap ?: return
        
        viewModelScope.launch(Dispatchers.Default) {
            val filtered = scanEngine.applyFilters(transformed, filterType)
            _uiState.update { 
                it.copy(
                    selectedFilter = filterType,
                    processedBitmap = filtered
                )
            }
        }
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
                // 1. Load Original Bitmap
                _uiState.update { it.copy(isLoading = true, uiState = CameraUiState.Transforming) }

                val uri = Uri.parse(imageUri)
                val originalBitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri).use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                }

                if (originalBitmap == null) {
                    onCaptureError("Không thể đọc dữ liệu ảnh chụp.")
                    return@launch
                }

                // Cập nhật ảnh gốc lên UI để làm hiệu ứng
                _uiState.update { it.copy(originalBitmap = originalBitmap) }

                // 2. Perspective Transform (Cắt phẳng)
                val (transformed, detected) = withContext(Dispatchers.Default) {
                    scanEngine.transformDocument(originalBitmap)
                }

                _uiState.update {
                    it.copy(
                        transformedBitmap = transformed,
                        processedBitmap = transformed,
                        uiState = CameraUiState.Filtering
                    )
                }

                // 3. Apply Default Filter (B&W)
                kotlinx.coroutines.delay(300)
                val filtered = withContext(Dispatchers.Default) {
                    scanEngine.applyFilters(transformed, ScanFilterType.B_W)
                }

                _uiState.update {
                    it.copy(
                        processedBitmap = filtered,
                        uiState = CameraUiState.OcrProcessing
                    )
                }

                // 4. OCR & PDF Generation
                val textTask = viewModelScope.launch(Dispatchers.Default) {
                    val text = scanEngine.extractText(transformed)
                    _uiState.update { it.copy(detectedText = text.ifBlank { "Không tìm thấy nội dung chữ." }) }
                }

                val pdfFile = withContext(Dispatchers.IO) {
                    scanEngine.createPdf(filtered, "Scan_${System.currentTimeMillis()}")
                }

                textTask.join() // Chờ OCR xong

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        uiState = CameraUiState.Success(imageUri, it.selectedMode)
                    )
                }
            } catch (e: Exception) {
                onCaptureError("Lỗi xử lý tài liệu: ${e.localizedMessage}")
            }
        }
    }

    fun extractTextFromPreview(context: Context, imageUri: String) {
        val currentText = _uiState.value.detectedText
        if (currentText.isNotEmpty() && currentText != "Không tìm thấy nội dung chữ.") {
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val uri = Uri.parse(imageUri)
                val bitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri).use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                }
                if (bitmap != null) {
                    val (transformed, _) = withContext(Dispatchers.Default) {
                        scanEngine.transformDocument(bitmap)
                    }
                    val text = withContext(Dispatchers.Default) {
                        scanEngine.extractText(transformed)
                    }
                    _uiState.update {
                        it.copy(
                            detectedText = text.ifBlank { "Không tìm thấy nội dung chữ." },
                            isLoading = false
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun saveDocument(pdfFileName: String = "Scan_${System.currentTimeMillis()}") {
        val bitmap = _uiState.value.processedBitmap ?: return
        
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            val pdfFile = scanEngine.createPdf(bitmap, pdfFileName)
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    pdfPath = pdfFile?.absolutePath
                )
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
        _uiState.update {
            it.copy(
                uiState = CameraUiState.Error(message),
                isLoading = false
            )
        }
    }
}
