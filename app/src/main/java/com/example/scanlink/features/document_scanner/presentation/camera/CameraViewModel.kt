package com.example.scanlink.features.document_scanner.presentation.camera

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.document_scanner.domain.entities.ScanFilterType
import com.example.scanlink.features.document_scanner.domain.usecases.ApplyScanFilterUseCase
import com.example.scanlink.features.document_scanner.domain.usecases.CreatePdfUseCase
import com.example.scanlink.features.document_scanner.domain.usecases.ExtractTextFromImageUseCase
import com.example.scanlink.features.document_scanner.domain.usecases.TransformDocumentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val transformDocumentUseCase: TransformDocumentUseCase,
    private val applyScanFilterUseCase: ApplyScanFilterUseCase,
    private val extractTextFromImageUseCase: ExtractTextFromImageUseCase,
    private val createPdfUseCase: CreatePdfUseCase
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
     * Hiệu ứng Laser Scan ngay sau khi chụp
     */
    fun onCaptureSuccess(context: Context, imageUri: String, onComplete: (String) -> Unit) {
        _uiState.update { 
            it.copy(isLoading = true, uiState = CameraUiState.Transforming) 
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(imageUri)
                
                val originalBitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri).use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)?.let { rotateImageIfRequired(context, it, uri) }
                    }
                } ?: throw Exception("Không thể đọc ảnh")

                _uiState.update { it.copy(originalBitmap = originalBitmap) }
                delay(400)

                val (transformed, _) = withContext(Dispatchers.Default) {
                    transformDocumentUseCase(originalBitmap)
                }
                
                val transformedBitmap = transformed as Bitmap

                _uiState.update { 
                    it.copy(
                        processedBitmap = transformedBitmap, 
                        uiState = CameraUiState.Filtering 
                    ) 
                }
                delay(600)

                val filtered = applyScanFilterUseCase(transformedBitmap, ScanFilterType.B_W) as Bitmap
                
                _uiState.update { 
                    it.copy(
                        processedBitmap = filtered,
                        uiState = CameraUiState.Success(imageUri)
                    ) 
                }

                withContext(Dispatchers.Main) {
                    onComplete(imageUri)
                }
            } catch (e: Exception) {
                onCaptureError("Lỗi xử lý: ${e.localizedMessage}")
            }
        }
    }

    /**
     * Trích xuất chữ từ trang Preview
     */
    fun processFilteredImageForOcr(context: Context, imageUri: String, onComplete: () -> Unit) {
        _uiState.update { 
            it.copy(isLoading = true, uiState = CameraUiState.OcrProcessing) 
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(imageUri)
                val bitmap = context.contentResolver.openInputStream(uri).use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                } ?: throw Exception("Lỗi đọc file ảnh")

                // Thực hiện OCR và tạo PDF
                val text = extractTextFromImageUseCase(bitmap)
                val pdfFile = createPdfUseCase(bitmap, "Scan_${System.currentTimeMillis()}")

                _uiState.update {
                    it.copy(
                        processedBitmap = bitmap,
                        detectedText = text,
                        pdfPath = pdfFile?.absolutePath,
                        isLoading = false,
                        uiState = CameraUiState.Success(imageUri)
                    )
                }

                withContext(Dispatchers.Main) { onComplete() }
            } catch (e: Exception) {
                onCaptureError("Lỗi trích xuất chữ: ${e.localizedMessage}")
            }
        }
    }

    fun onFilterSelected(filterType: ScanFilterType) {
        val bitmap = _uiState.value.processedBitmap ?: return
        viewModelScope.launch(Dispatchers.Default) {
            val filtered = applyScanFilterUseCase(bitmap, filterType)
            _uiState.update { 
                it.copy(selectedFilter = filterType, processedBitmap = filtered as? Bitmap)
            }
        }
    }

    private fun rotateImageIfRequired(context: Context, img: Bitmap, selectedImage: Uri): Bitmap {
        val input = context.contentResolver.openInputStream(selectedImage)
        val ei = input?.use { ExifInterface(it) } ?: return img
        return when (ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
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

    fun saveDocument() {
        val bitmap = _uiState.value.processedBitmap ?: return
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            val pdfFile = createPdfUseCase(bitmap, "ScanLink_${System.currentTimeMillis()}")
            _uiState.update { it.copy(isLoading = false, pdfPath = pdfFile?.absolutePath) }
        }
    }
}
