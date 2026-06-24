package com.example.scanlink.features.document_scanner.presentation.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
                        processedBitmap = transformed, 
                        uiState = CameraUiState.Filtering 
                    ) 
                }

                // 3. Apply Black & White Filter (Khử màu - Hiệu ứng CamScanner)
                // delay nhẹ để người dùng kịp thấy ảnh đã được cắt phẳng trước khi đổi màu
                kotlinx.coroutines.delay(500) 
                
                val filtered = withContext(Dispatchers.Default) {
                    scanEngine.applyFilters(transformed)
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
                        uiState = CameraUiState.Success(imageUri, it.selectedMode),
                        pdfPath = pdfFile?.absolutePath
                    )
                }

            } catch (e: Exception) {
                onCaptureError("Lỗi xử lý tài liệu: ${e.localizedMessage}")
            }
        }
    }

    fun onCaptureError(message: String) {
        _uiState.value = _uiState.value.copy(
            uiState = CameraUiState.Error(message),
            isLoading = false
        )
    }
}
