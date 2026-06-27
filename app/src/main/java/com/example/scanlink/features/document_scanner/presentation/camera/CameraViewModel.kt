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

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val transformDocumentUseCase: TransformDocumentUseCase,
    private val applyScanFilterUseCase: ApplyScanFilterUseCase,
    private val extractTextFromImageUseCase: ExtractTextFromImageUseCase,
    private val createPdfUseCase: CreatePdfUseCase,
    private val documentRepository: com.example.scanlink.features.document_scanner.domain.repositories.DocumentRepository,
    private val apiService: com.example.scanlink.core.network.ApiService
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
     * Lưu tài liệu cục bộ và tải lên máy chủ
     */
    fun saveAndUploadDocument(context: Context, imageUri: String, onComplete: (String) -> Unit) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(imageUri)
                val bitmap = context.contentResolver.openInputStream(uri).use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)
                } ?: throw Exception("Lỗi đọc tệp ảnh")

                // 1. Tạo file PDF
                val pdfFile = createPdfUseCase(bitmap, "Scan_${System.currentTimeMillis()}")
                    ?: throw Exception("Lỗi tạo file PDF")

                android.util.Log.d("ScanLink", "Uploading camera PDF. Size: ${pdfFile.length()} bytes")
                val requestFile = pdfFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                val filePart = okhttp3.MultipartBody.Part.createFormData("file", pdfFile.name, requestFile)
                val cleanTitle = pdfFile.name.substringBeforeLast(".")
                val titlePart = okhttp3.MultipartBody.Part.createFormData("title", cleanTitle)
                val textPart = okhttp3.MultipartBody.Part.createFormData("extractedText", "")

                val response = apiService.uploadDocument(filePart, titlePart, textPart).execute()

                val now = System.currentTimeMillis()
                val finalDocId = if (response.isSuccessful && response.body()?.data != null) {
                    val serverDoc = response.body()!!.data!!

                    // Lưu local DB bằng chính ID được sinh bởi Server
                    val newDoc = com.example.scanlink.features.document_scanner.domain.entities.Document(
                        id = serverDoc.id,
                        ownerUid = serverDoc.ownerUid,
                        title = serverDoc.title,
                        storageUrl = serverDoc.storageUrl,
                        fileSize = pdfFile.length(),
                        extractedText = null,
                        pdfPath = pdfFile.absolutePath,
                        createdAt = now,
                        updatedAt = now,
                        isSynced = true,
                        pageCount = 1,
                        mimeType = "application/pdf",
                        thumbnailPath = imageUri,
                        lastModified = now
                    )
                    val page = com.example.scanlink.features.document_scanner.domain.entities.Page(
                        id = java.util.UUID.randomUUID().toString(),
                        documentId = serverDoc.id,
                        pageNumber = 1,
                        imagePath = imageUri,
                        ocrText = null,
                        createdAt = now
                    )
                    documentRepository.saveDocument(newDoc, listOf(page))
                    serverDoc.id
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("ScanLink", "Upload failed. Code: ${response.code()}, Error: $errorBody")

                    // Nếu upload lỗi, lưu offline bằng ID client tự sinh
                    val localId = java.util.UUID.randomUUID().toString()
                    val newDoc = com.example.scanlink.features.document_scanner.domain.entities.Document(
                        id = localId,
                        ownerUid = null,
                        title = pdfFile.name,
                        storageUrl = null,
                        fileSize = pdfFile.length(),
                        extractedText = null,
                        pdfPath = pdfFile.absolutePath,
                        createdAt = now,
                        updatedAt = now,
                        isSynced = false,
                        pageCount = 1,
                        mimeType = "application/pdf",
                        thumbnailPath = imageUri,
                        lastModified = now
                    )
                    val page = com.example.scanlink.features.document_scanner.domain.entities.Page(
                        id = java.util.UUID.randomUUID().toString(),
                        documentId = localId,
                        pageNumber = 1,
                        imagePath = imageUri,
                        ocrText = null,
                        createdAt = now
                    )
                    documentRepository.saveDocument(newDoc, listOf(page))
                    localId
                }

                _uiState.update {
                    it.copy(
                        processedBitmap = bitmap,
                        pdfPath = pdfFile.absolutePath,
                        isLoading = false
                    )
                }

                withContext(Dispatchers.Main) { onComplete(finalDocId) }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
                onCaptureError("Lỗi lưu tài liệu: ${e.localizedMessage}")
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
