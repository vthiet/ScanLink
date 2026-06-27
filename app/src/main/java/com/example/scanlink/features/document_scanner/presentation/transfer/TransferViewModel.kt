package com.example.scanlink.features.document_scanner.presentation.transfer

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.core.network.ApiService
import com.example.scanlink.core.ui.model.FileType
import com.example.scanlink.core.ui.model.RecentFile
import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page
import com.example.scanlink.features.document_scanner.domain.repositories.DocumentRepository
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class TransferUiState(
    val uploadingFiles: List<RecentFile> = emptyList(),
    val recentFiles: List<RecentFile> = emptyList(),
    val actionMessage: String? = null
)

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val apiService: ApiService,
    private val documentRepository: DocumentRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransferUiState())
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    init {
        observeLocalDocuments()
    }

    private fun observeLocalDocuments() {
        viewModelScope.launch {
            documentRepository.getDocumentsFlow().collect { docs ->
                val recent = docs.map { doc ->
                    val fileType = when {
                        doc.title.endsWith(".pdf", ignoreCase = true) -> FileType.PDF
                        doc.title.endsWith(".docx", ignoreCase = true) -> FileType.DOCX
                        doc.title.endsWith(".jpg", ignoreCase = true) || doc.title.endsWith(".jpeg", ignoreCase = true) || doc.title.endsWith(".png", ignoreCase = true) -> FileType.JPG
                        else -> FileType.OTHER
                    }
                    RecentFile(
                        id = doc.id,
                        name = doc.title,
                        type = fileType,
                        createdAt = formatDate(doc.createdAt),
                        sizeLabel = formatFileSize(doc.fileSize),
                        statusText = if (doc.isSynced) "Đã đồng bộ" else "Chưa đồng bộ",
                        statusColor = if (doc.isSynced) Color(0xFF00CFA4) else Color(0xFFFF9800)
                    )
                }
                _uiState.update { it.copy(recentFiles = recent) }
            }
        }
    }

    fun uploadFile(uri: Uri) {
        viewModelScope.launch {
            val fileName = getFileName(appContext, uri) ?: "upload_${System.currentTimeMillis()}"
            val tempFile = File(appContext.cacheDir, fileName)

            val mimeType = appContext.contentResolver.getType(uri) ?: "application/octet-stream"
            val fileType = when {
                fileName.endsWith(".pdf", ignoreCase = true) -> FileType.PDF
                fileName.endsWith(".docx", ignoreCase = true) -> FileType.DOCX
                fileName.endsWith(".jpg", ignoreCase = true) || fileName.endsWith(".jpeg", ignoreCase = true) || fileName.endsWith(".png", ignoreCase = true) -> FileType.JPG
                else -> FileType.OTHER
            }

            val uploadingFile = RecentFile(
                id = UUID.randomUUID().toString(),
                name = fileName,
                type = fileType,
                createdAt = "Vừa xong",
                sizeLabel = "Đang tính...",
                statusText = "Đang tải lên...",
                statusColor = Color(0xFF3498DB),
                uploadProgress = 0.1f
            )

            _uiState.update { it.copy(uploadingFiles = it.uploadingFiles + uploadingFile) }

            val success = withContext(Dispatchers.IO) {
                try {
                    appContext.contentResolver.openInputStream(uri)?.use { inputStream ->
                        tempFile.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    // Nếu là file ảnh, tiến hành nén trước khi upload
                    var finalUploadFile = tempFile
                    if (mimeType.startsWith("image/", ignoreCase = true)) {
                        val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath)
                        if (bitmap != null) {
                            val maxDim = 800
                            val w = bitmap.width
                            val h = bitmap.height
                            val scaled = if (w > maxDim || h > maxDim) {
                                val ratio = w.toFloat() / h.toFloat()
                                val newW = if (ratio > 1) maxDim else (maxDim * ratio).toInt()
                                val newH = if (ratio > 1) (maxDim / ratio).toInt() else maxDim
                                Bitmap.createScaledBitmap(bitmap, newW, newH, true)
                            } else {
                                bitmap
                            }

                            val compressedName = "compressed_${fileName.substringBeforeLast(".")}.jpg"
                            val compressedFile = File(appContext.cacheDir, compressedName)
                            compressedFile.outputStream().use { outStream ->
                                scaled.compress(Bitmap.CompressFormat.JPEG, 50, outStream)
                            }
                            finalUploadFile = compressedFile
                        }
                    }

                    android.util.Log.d("ScanLink", "Uploading transfer file. Size: ${finalUploadFile.length()} bytes")
                    val requestFile = finalUploadFile.asRequestBody(mimeType.toMediaTypeOrNull())
                    val filePart = MultipartBody.Part.createFormData("file", finalUploadFile.name, requestFile)
                    val cleanTitle = finalUploadFile.name.substringBeforeLast(".")
                    val titlePart = MultipartBody.Part.createFormData("title", cleanTitle)
                    val textPart = MultipartBody.Part.createFormData("extractedText", "")

                    val response = apiService.uploadDocument(filePart, titlePart, textPart).execute()

                    if (response.isSuccessful && response.body()?.data != null) {
                        val serverDoc = response.body()!!.data!!

                        // Lưu vào local database
                        val now = System.currentTimeMillis()
                        val newDoc = Document(
                            id = UUID.randomUUID().toString(),
                            ownerUid = null,
                            title = finalUploadFile.name,
                            storageUrl = serverDoc.storageUrl,
                            fileSize = finalUploadFile.length(),
                            extractedText = null,
                            pdfPath = finalUploadFile.absolutePath,
                            createdAt = now,
                            updatedAt = now,
                            isSynced = true,
                            pageCount = 1,
                            mimeType = mimeType,
                            thumbnailPath = finalUploadFile.absolutePath,
                            lastModified = now
                        )
                        val page = Page(
                            id = UUID.randomUUID().toString(),
                            documentId = newDoc.id,
                            pageNumber = 1,
                            imagePath = finalUploadFile.absolutePath,
                            ocrText = null,
                            createdAt = now
                        )
                        documentRepository.saveDocument(newDoc, listOf(page))
                        true
                    } else {
                        val errorBody = response.errorBody()?.string()
                        android.util.Log.e("ScanLink", "Transfer upload failed. Code: ${response.code()}, Error: $errorBody")
                        false
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            }

            _uiState.update { state ->
                state.copy(
                    uploadingFiles = state.uploadingFiles.filter { it.id != uploadingFile.id },
                    actionMessage = if (success) "Tải lên thành công!" else "Tải lên thất bại."
                )
            }
        }
    }

    fun consumeActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    private fun getFileName(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) result = cursor.getString(index)
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun formatDate(timestamp: Long): String {
        return try {
            val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            format.format(Date(timestamp))
        } catch (_: Exception) {
            "Gần đây"
        }
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.toDouble())).toInt()
        val group = if (digitGroups in units.indices) digitGroups else 0
        return String.format(Locale.getDefault(), "%.1f %s", size / Math.pow(1024.toDouble(), group.toDouble()), units[group])
    }
}
