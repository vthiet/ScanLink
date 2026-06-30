package com.example.scanlink.features.dashboard.presentation.home

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.core.network.ApiService
import com.example.scanlink.core.network.models.DocumentResponse
import com.example.scanlink.core.ui.model.FileType
import com.example.scanlink.core.ui.model.RecentFile
import com.example.scanlink.features.dashboard.domain.usecases.GetDashboardDataUseCase
import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page
import com.example.scanlink.features.document_scanner.domain.repositories.DocumentRepository
import com.example.scanlink.features.document_scanner.domain.usecases.CreateMultiImagePdfUseCase
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val documents: List<RecentFile> = emptyList(),
    val errorMessage: String? = null,
    val createdDocumentId: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val createMultiImagePdfUseCase: CreateMultiImagePdfUseCase,
    private val getDashboardDataUseCase: GetDashboardDataUseCase,
    private val documentRepository: DocumentRepository,
    private val apiService: ApiService,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDocuments()
    }

    fun loadDocuments() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            getDashboardDataUseCase()
                .onSuccess { pageResponse ->
                    val recentFiles = pageResponse.content.map { it.toRecentFile() }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            documents = recentFiles
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Loi tai tai lieu tu server"
                        )
                    }
                }
        }
    }

    fun createPdfFromUris(context: Context, uris: List<Uri>) {
        if (uris.isEmpty()) return

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val bitmaps = withContext(Dispatchers.IO) {
                    uris.mapNotNull { uri ->
                        context.contentResolver.openInputStream(uri)?.use {
                            BitmapFactory.decodeStream(it)
                        }
                    }
                }

                if (bitmaps.isEmpty()) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Khong doc duoc anh da chon")
                    }
                    Toast.makeText(context, "Khong doc duoc anh da chon", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val pdfFile = createMultiImagePdfUseCase(
                    bitmaps,
                    "ScanLink_Imported_${System.currentTimeMillis()}"
                )

                if (pdfFile == null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Loi khi tao PDF") }
                    Toast.makeText(context, "Loi khi tao PDF", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val documentId = saveImportedPdf(pdfFile, uris)
                Toast.makeText(context, "Da tao PDF thanh cong: ${pdfFile.name}", Toast.LENGTH_LONG).show()
                loadDocuments()
                _uiState.update { it.copy(isLoading = false, createdDocumentId = documentId) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Loi import anh"
                    )
                }
                Toast.makeText(context, "Loi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun saveImportedPdf(pdfFile: File, imageUris: List<Uri>): String {
        return withContext(Dispatchers.IO) {
            val requestFile = pdfFile.asRequestBody("application/pdf".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", pdfFile.name, requestFile)
            val titlePart = MultipartBody.Part.createFormData("title", pdfFile.name.substringBeforeLast("."))
            val textPart = MultipartBody.Part.createFormData("extractedText", "")
            val now = System.currentTimeMillis()

            val uploadedDocument = runCatching {
                val response = apiService.uploadDocument(filePart, titlePart, textPart).execute()
                if (response.isSuccessful) response.body()?.data else null
            }.getOrNull()

            val documentId = uploadedDocument?.id?.takeIf { it.isNotBlank() } ?: UUID.randomUUID().toString()
            val document = Document(
                id = documentId,
                ownerUid = uploadedDocument?.ownerUid ?: firebaseAuth.currentUser?.uid,
                title = uploadedDocument?.title?.takeIf { it.isNotBlank() } ?: pdfFile.name,
                storageUrl = uploadedDocument?.storageUrl,
                fileSize = pdfFile.length(),
                extractedText = null,
                pdfPath = pdfFile.absolutePath,
                createdAt = now,
                updatedAt = now,
                isSynced = uploadedDocument != null,
                pageCount = imageUris.size,
                mimeType = "application/pdf",
                thumbnailPath = imageUris.firstOrNull()?.toString(),
                lastModified = now
            )
            val pages = imageUris.mapIndexed { index, uri ->
                Page(
                    id = UUID.randomUUID().toString(),
                    documentId = documentId,
                    pageNumber = index + 1,
                    imagePath = uri.toString(),
                    ocrText = null,
                    createdAt = now
                )
            }

            documentRepository.saveDocument(document, pages).getOrThrow()
            documentId
        }
    }

    fun consumeCreatedDocument() {
        _uiState.update { it.copy(createdDocumentId = null) }
    }
}

private fun DocumentResponse.toRecentFile(): RecentFile {
    val fileTitle = title.orEmpty()
    val fileType = when {
        fileTitle.endsWith(".pdf", ignoreCase = true) -> FileType.PDF
        fileTitle.endsWith(".docx", ignoreCase = true) -> FileType.DOCX
        fileTitle.endsWith(".jpg", ignoreCase = true) ||
            fileTitle.endsWith(".jpeg", ignoreCase = true) ||
            fileTitle.endsWith(".png", ignoreCase = true) -> FileType.JPG
        else -> FileType.OTHER
    }

    val sizeLabel = formatFileSize(fileSize ?: 0L)
    val dateStr = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val parsedDate = inputFormat.parse(createdAt.orEmpty()) ?: Date()
        outputFormat.format(parsedDate)
    } catch (_: Exception) {
        createdAt?.take(10)?.replace("-", "/") ?: ""
    }

    return RecentFile(
        id = id.orEmpty(),
        name = fileTitle,
        type = fileType,
        createdAt = dateStr,
        sizeLabel = sizeLabel,
        statusText = "Dong bo",
        statusColor = Color(0xFF00CFA4)
    )
}

private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.toDouble())).toInt()
    val group = if (digitGroups in units.indices) digitGroups else 0
    return String.format(
        Locale.getDefault(),
        "%.1f %s",
        size / Math.pow(1024.toDouble(), group.toDouble()),
        units[group]
    )
}
