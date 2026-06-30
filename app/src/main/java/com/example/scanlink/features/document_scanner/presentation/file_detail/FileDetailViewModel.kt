package com.example.scanlink.features.document_scanner.presentation.file_detail

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.repositories.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@HiltViewModel
class FileDetailViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val apiService: com.example.scanlink.core.network.ApiService,
    private val extractTextFromImageUseCase: com.example.scanlink.features.document_scanner.domain.usecases.ExtractTextFromImageUseCase,
    private val syncDocumentUseCase: com.example.scanlink.features.document_scanner.domain.usecases.SyncDocumentUseCase,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val documentId: String = savedStateHandle["documentId"] ?: "preview-file"

    private val _uiState = MutableStateFlow(FileDetailUiState())
    val uiState: StateFlow<FileDetailUiState> = _uiState.asStateFlow()

    init {
        loadDocument()
    }

    fun loadDocument() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = documentRepository.getDocumentById(documentId)
            var document = result.getOrNull()

            if (document == null) {
                try {
                    val apiResponse = withContext(Dispatchers.IO) {
                        apiService.getDocumentDetail(documentId).execute()
                    }
                    if (apiResponse.isSuccessful) {
                        val docResponse = apiResponse.body()?.data
                        if (docResponse != null) {
                            document = Document(
                                id = docResponse.id,
                                ownerUid = docResponse.ownerUid,
                                title = docResponse.title,
                                storageUrl = docResponse.storageUrl,
                                fileSize = docResponse.fileSize,
                                extractedText = docResponse.extractedText,
                                pdfPath = null,
                                createdAt = parseIsoDate(docResponse.createdAt),
                                updatedAt = parseIsoDate(docResponse.updatedAt),
                                isSynced = true,
                                pageCount = 1,
                                mimeType = "application/pdf"
                            )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            if (document == null) {
                document = fakeFileDetailDocument(documentId)
            }

            _uiState.update {
                it.copy(
                    isLoading = false,
                    document = document,
                    renameValue = document.title,
                    errorMessage = if (document == null) result.exceptionOrNull()?.localizedMessage else null
                )
            }
        }
    }

    private fun parseIsoDate(isoStr: String): Long {
        return try {
            val format = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            format.parse(isoStr)?.time ?: System.currentTimeMillis()
        } catch (_: Exception) {
            System.currentTimeMillis()
        }
    }

    fun showRenameDialog() {
        _uiState.update {
            it.copy(
                isRenameDialogVisible = true,
                renameValue = it.document?.title.orEmpty()
            )
        }
    }

    fun hideRenameDialog() {
        _uiState.update { it.copy(isRenameDialogVisible = false) }
    }

    fun onRenameValueChange(value: String) {
        _uiState.update { it.copy(renameValue = value) }
    }

    fun confirmRename() {
        val state = _uiState.value
        val document = state.document ?: return
        val newTitle = state.renameValue.trim()
        if (newTitle.isBlank()) return

        viewModelScope.launch {
            documentRepository.renameDocument(document.id, newTitle)
            _uiState.update {
                it.copy(
                    document = document.copy(
                        title = newTitle,
                        updatedAt = System.currentTimeMillis(),
                        lastModified = System.currentTimeMillis()
                    ),
                    isRenameDialogVisible = false,
                    actionMessage = "Renamed"
                )
            }
        }
    }

    fun showDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = true) }
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = false) }
    }

    fun confirmDelete(onDeleted: () -> Unit) {
        val document = _uiState.value.document ?: return
        viewModelScope.launch {
            deleteLocalFile(document)
            documentRepository.deleteDocument(document.id)
            withContext(Dispatchers.IO) {
                runCatching { apiService.deleteDocument(document.id).execute() }
            }
            _uiState.update { it.copy(isDeleteDialogVisible = false, actionMessage = "Deleted") }
            onDeleted()
        }
    }

    fun duplicateDocument() {
        val document = _uiState.value.document ?: return
        viewModelScope.launch {
            copyLocalFileForDuplicate(document)
            documentRepository.duplicateDocument(document.id)
            _uiState.update { it.copy(actionMessage = "Duplicated") }
        }
    }

    // ── SHARING DIALOG LOGIC ──────────────────────────────────────────────────
    fun showShareOptions() {
        _uiState.update { it.copy(isShareOptionsVisible = true) }
    }

    fun hideShareOptions() {
        _uiState.update { it.copy(isShareOptionsVisible = false) }
    }

    fun showPublicLinkDialog() {
        _uiState.update {
            it.copy(
                isShareOptionsVisible = false,
                isPublicLinkDialogVisible = true,
                sharePasswordValue = "",
                shareExpireDaysValue = ""
            )
        }
    }

    fun hidePublicLinkDialog() {
        _uiState.update { it.copy(isPublicLinkDialogVisible = false) }
    }

    fun showPrivateAccessDialog() {
        _uiState.update {
            it.copy(
                isShareOptionsVisible = false,
                isPrivateAccessDialogVisible = true,
                shareEmailValue = "",
                shareRoleValue = "VIEWER"
            )
        }
    }

    fun hidePrivateAccessDialog() {
        _uiState.update { it.copy(isPrivateAccessDialogVisible = false) }
    }

    fun onSharePasswordChange(value: String) {
        _uiState.update { it.copy(sharePasswordValue = value) }
    }

    fun onShareExpireDaysChange(value: String) {
        _uiState.update { it.copy(shareExpireDaysValue = value) }
    }

    fun onShareEmailChange(value: String) {
        _uiState.update { it.copy(shareEmailValue = value) }
    }

    fun onShareRoleChange(value: String) {
        _uiState.update { it.copy(shareRoleValue = value) }
    }

    fun confirmCreatePublicLink() {
        val document = _uiState.value.document ?: return
        val password = _uiState.value.sharePasswordValue.takeIf { it.isNotBlank() }
        val expireDays = _uiState.value.shareExpireDaysValue.toIntOrNull()

        _uiState.update { it.copy(isSharingLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val finalDoc = syncDocumentUseCase(document)
                val request = com.example.scanlink.core.network.models.CreatePublicShareRequest(
                    documentId = finalDoc.id,
                    password = password,
                    expireInDays = expireDays
                )
                val response = apiService.createPublicShareLink(request).execute()
                if (response.isSuccessful) {
                    val shareLink = response.body()?.data?.shareUrl ?: "https://scanlink.com/share/${response.body()?.data?.hashToken}"
                    _uiState.update {
                        it.copy(
                            isSharingLoading = false,
                            document = finalDoc,
                            generatedShareLink = shareLink,
                            isPublicLinkDialogVisible = false,
                            isPublicLinkSuccessVisible = true
                        )
                    }
                } else {
                    throw Exception("Lỗi từ server: ${response.code()}")
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSharingLoading = false,
                        actionMessage = "Tạo link thất bại: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    fun confirmGrantPrivatePermission() {
        val document = _uiState.value.document ?: return
        val email = _uiState.value.shareEmailValue.trim()
        val role = _uiState.value.shareRoleValue

        if (email.isBlank()) return

        _uiState.update { it.copy(isSharingLoading = true) }

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val finalDoc = syncDocumentUseCase(document)
                val request = com.example.scanlink.core.network.models.GrantPrivatePermissionRequest(
                    documentId = finalDoc.id,
                    shareToEmail = email,
                    role = role
                )
                val response = apiService.grantPrivatePermission(request).execute()
                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isSharingLoading = false,
                            document = finalDoc,
                            isPrivateAccessDialogVisible = false,
                            actionMessage = "Đã cấp quyền cho $email thành công!"
                        )
                    }
                } else {
                    throw Exception("Lỗi từ server: ${response.code()}")
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSharingLoading = false,
                        actionMessage = "Cấp quyền thất bại: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    fun hidePublicLinkSuccess() {
        _uiState.update { it.copy(isPublicLinkSuccessVisible = false, generatedShareLink = null) }
    }

    fun extractTextFromDocument() {
        val document = _uiState.value.document ?: return
        val imagePath = document.thumbnailPath ?: document.pages.firstOrNull()?.imagePath
        if (imagePath.isNullOrBlank()) {
            _uiState.update { it.copy(actionMessage = "Không tìm thấy đường dẫn ảnh để quét OCR.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uri = Uri.parse(imagePath)
                val bitmap = try {
                    appContext.contentResolver.openInputStream(uri).use { inputStream ->
                        BitmapFactory.decodeStream(inputStream)
                    }
                } catch (_: Exception) {
                    val file = File(imagePath)
                    if (file.exists()) {
                        BitmapFactory.decodeFile(file.absolutePath)
                    } else null
                }

                if (bitmap == null) {
                    throw Exception("Không thể tải ảnh tài liệu")
                }

                // Chạy OCR
                val extractedText = extractTextFromImageUseCase(bitmap)

                // Cập nhật local DB
                val updatedDoc = document.copy(extractedText = extractedText)
                documentRepository.saveDocument(updatedDoc, document.pages)

                // Upload văn bản trích xuất lên server nếu tệp đã được đồng bộ
                try {
                    val file = document.pdfPath?.let(::File)?.takeIf { it.exists() }
                    if (file != null) {
                        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                        val filePart = okhttp3.MultipartBody.Part.createFormData("file", file.name, requestFile)
                        val cleanTitle = file.name.substringBeforeLast(".")
                        val titlePart = okhttp3.MultipartBody.Part.createFormData("title", cleanTitle)
                        val textPart = okhttp3.MultipartBody.Part.createFormData("extractedText", extractedText)

                        apiService.uploadDocument(filePart, titlePart, textPart).execute()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        document = updatedDoc,
                        actionMessage = "Trích xuất văn bản (OCR) thành công!"
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        actionMessage = "OCR thất bại: ${e.localizedMessage ?: "Lỗi không xác định"}"
                    )
                }
            }
        }
    }

    fun shareDocument(context: Context) {
        val document = _uiState.value.document ?: return
        val file = document.filePath?.let(::File)?.takeIf { it.exists() }
        val intent = if (file != null) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            Intent(Intent.ACTION_SEND).apply {
                type = document.mimeType ?: "application/octet-stream"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } else {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "${document.title}\n${document.extractedText.orEmpty()}")
            }
        }
        context.startActivity(Intent.createChooser(intent, "Share file"))
    }

    fun exportPdf(context: Context) {
        _uiState.update { it.copy(actionMessage = "PDF export ready") }
        shareDocument(context)
    }

    fun exportImage(context: Context) {
        _uiState.update { it.copy(actionMessage = "Image export ready") }
        shareDocument(context)
    }

    fun printDocument() {
        _uiState.update { it.copy(actionMessage = "Print action selected") }
    }

    fun convertDocument() {
        _uiState.update { it.copy(actionMessage = "Export PDF action selected") }
    }

    fun copyOcrText(context: Context) {
        val text = _uiState.value.document?.extractedText.orEmpty()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Recognized Text", text))
        _uiState.update { it.copy(actionMessage = "OCR copied") }
    }

    fun consumeActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    private fun deleteLocalFile(document: Document) {
        document.filePath?.let { path ->
            runCatching { File(path).takeIf { it.exists() }?.delete() }
        }
    }

    private fun copyLocalFileForDuplicate(document: Document) {
        val source = document.filePath?.let(::File)?.takeIf { it.exists() } ?: return
        val target = File(source.parentFile, "${source.nameWithoutExtension}_${UUID.randomUUID()}.${source.extension}")
        runCatching { source.copyTo(target, overwrite = false) }
    }
}

private val Document.filePath: String?
    get() = pdfPath ?: thumbnailPath ?: pages.firstOrNull()?.imagePath
