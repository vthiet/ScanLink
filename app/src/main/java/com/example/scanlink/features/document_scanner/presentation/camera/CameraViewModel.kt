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
import kotlinx.coroutines.delay
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

    /**
     * Quy trình quét ngay sau khi chụp (Hiệu ứng Laser Scan)
     * context: Context để đọc Uri
     * imageUri: Đường dẫn ảnh vừa chụp
     * onComplete: Callback chuyển sang màn hình Preview
     */
    fun onCaptureSuccess(context: Context, imageUri: String, onComplete: () -> Unit) {
        _uiState.update { 
            it.copy(
                isLoading = true, 
                capturedImageUri = imageUri,
                uiState = CameraUiState.Transforming 
            ) 
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(imageUri)
                
                // 1. Load và xoay ảnh đúng chiều
                val originalBitmap = withContext(Dispatchers.IO) {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    bitmap?.let { rotateImageIfRequired(context, it, uri) }
                }

                if (originalBitmap == null) {
                    onCaptureError("Không thể đọc dữ liệu ảnh.")
                    return@launch
                }

                _uiState.update { it.copy(originalBitmap = originalBitmap) }
                delay(400) // Hiệu ứng chờ ban đầu

                // 2. Căn chỉnh tài liệu (Transform)
                val (transformed, _) = withContext(Dispatchers.Default) {
                    scanEngine.transformDocument(originalBitmap)
                }
                
                _uiState.update { 
                    it.copy(
                        processedBitmap = transformed, 
                        uiState = CameraUiState.Filtering 
                    ) 
                }
                delay(600) // Tia laser chạy qua ảnh đã cắt

                // 3. Lọc màu (B&W mặc định)
                val filtered = withContext(Dispatchers.Default) {
                    scanEngine.applyFilters(transformed, ScanFilterType.B_W)
                }
                
                _uiState.update { 
                    it.copy(
                        processedBitmap = filtered,
                        uiState = CameraUiState.Success(imageUri)
                    ) 
                }

                // Chuyển sang trang Preview
                withContext(Dispatchers.Main) {
                    onComplete()
                }

            } catch (e: Exception) {
                onCaptureError("Lỗi xử lý: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Nhận ảnh từ trang Preview, thực hiện OCR XONG rồi mới chuyển sang trang kết quả
     */
    fun processFilteredImageForOcr(context: Context, imageUri: String, onComplete: () -> Unit) {
        _uiState.update { 
            it.copy(
                isLoading = true, 
                uiState = CameraUiState.OcrProcessing 
            ) 
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(imageUri)
                val bitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri).use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                }

                if (bitmap == null) {
                    onCaptureError("Lỗi đọc file ảnh.")
                    return@launch
                }

                // 1. Thực hiện OCR
                val text = scanEngine.extractText(bitmap)

                // 2. Tạo PDF
                val pdfFile = scanEngine.createPdf(bitmap, "Scan_${System.currentTimeMillis()}")

                // 3. Cập nhật State
                _uiState.update {
                    it.copy(
                        processedBitmap = bitmap,
                        detectedText = text.ifBlank { "Không tìm thấy nội dung chữ." },
                        pdfPath = pdfFile?.absolutePath,
                        isLoading = false,
                        uiState = CameraUiState.Success(imageUri)
                    )
                }

                // 4. Chỉ chuyển trang khi ĐÃ XONG
                withContext(Dispatchers.Main) {
                    onComplete()
                }

            } catch (e: Exception) {
                onCaptureError("Lỗi trích xuất chữ: ${e.localizedMessage}")
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
        val matrix = Matrix().apply { postRotate(degree) }
        return Bitmap.createBitmap(img, 0, 0, img.width, img.height, matrix, true)
    }

    fun onCaptureError(message: String) {
        _uiState.update { it.copy(uiState = CameraUiState.Error(message), isLoading = false) }
    }

    fun saveDocument() {}
}
