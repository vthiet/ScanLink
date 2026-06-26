package com.example.scanlink.features.document_scanner.presentation.camera

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.document_scanner.data.engine.ScanEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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

    fun onCaptureStarted() {
        _uiState.update {
            it.copy(
                uiState = CameraUiState.Capturing,
                isLoading = true
            )
        }
    }

    /**
     * Camera only captures image. Do NOT run OCR here.
     * Flow: Camera -> Preview -> user taps Extract Text -> OCR.
     */
    fun onCaptureSuccess(imageUri: String) {
        _uiState.update {
            it.copy(
                uiState = CameraUiState.Success(imageUri, it.selectedMode),
                isLoading = false,
                capturedImageUri = imageUri,
                detectedText = "",
                processedBitmap = null,
                pdfPath = null
            )
        }
    }

    fun extractTextFromPreview(context: Context, imageUri: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(imageUri)
                val bitmap = context.contentResolver.openInputStream(uri).use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }

                if (bitmap == null) {
                    onCaptureError("Không thể đọc dữ liệu ảnh chụp.")
                    return@launch
                }

                val result = scanEngine.fullProcess(
                    bitmap = bitmap,
                    pdfFileName  = "Scan_${System.currentTimeMillis()}"
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        processedBitmap = result.processedBitmap,
                        detectedText = result.extractedText.ifBlank { "Không tìm thấy nội dung chữ." },
                        pdfPath = result.pdfFile?.absolutePath
                    )
                }
            } catch (e: Exception) {
                onCaptureError("Lỗi xử lý tài liệu: ${e.localizedMessage}")
            }
        }
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
