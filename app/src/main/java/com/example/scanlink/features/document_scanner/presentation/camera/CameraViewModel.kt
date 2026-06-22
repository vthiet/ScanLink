package com.example.scanlink.features.document_scanner.presentation.camera

import android.content.Context
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
        _uiState.update {
            it.copy(
                uiState = CameraUiState.Success(imageUri, it.selectedMode),
                isLoading = true
            )
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(imageUri)
                val bitmap = context.contentResolver.openInputStream(uri).use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                }

                if (bitmap != null) {
                    val result = scanEngine.fullProcess(bitmap, "Scan_${System.currentTimeMillis()}")

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            processedBitmap = result.processedBitmap,
                            detectedText = result.extractedText.ifBlank { "Không tìm thấy nội dung chữ." },
                            pdfPath = result.pdfFile?.absolutePath
                        )
                    }
                } else {
                    onCaptureError("Không thể đọc dữ liệu ảnh chụp.")
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