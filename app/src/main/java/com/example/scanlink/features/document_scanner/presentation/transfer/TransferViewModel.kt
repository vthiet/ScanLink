package com.example.scanlink.features.document_scanner.presentation.transfer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.core.network.ApiService
import com.example.scanlink.core.network.models.CreatePublicShareRequest
import com.example.scanlink.core.network.models.GrantPrivatePermissionRequest
import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page
import com.example.scanlink.features.document_scanner.domain.repositories.DocumentRepository
import com.example.scanlink.features.document_scanner.presentation.transfer.model.PrivateShareTabState
import com.example.scanlink.features.document_scanner.presentation.transfer.model.PublicLinkItem
import com.example.scanlink.features.document_scanner.presentation.transfer.model.PublicShareTabState
import com.example.scanlink.features.document_scanner.presentation.transfer.model.SharePermission
import com.example.scanlink.features.document_scanner.presentation.transfer.model.SharedUserItem
import com.example.scanlink.features.document_scanner.presentation.transfer.model.TransferDocumentOption
import com.example.scanlink.features.document_scanner.presentation.transfer.model.TransferTab
import com.example.scanlink.features.document_scanner.presentation.transfer.model.TransferUiState
import com.example.scanlink.features.document_scanner.presentation.transfer.model.UploadDocumentItem
import com.example.scanlink.features.document_scanner.presentation.transfer.model.UploadState
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TransferViewModel @Inject constructor(
    private val apiService: ApiService,
    private val documentRepository: DocumentRepository,
    @ApplicationContext private val appContext: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val initialTab = TransferTab.fromRoute(savedStateHandle.get<String>("tab"))
    private val initialDocumentId: String? = savedStateHandle.get<String>("documentId")
        ?.takeUnless { it == "none" }

    private val _uiState = MutableStateFlow(
        TransferUiState(
            selectedTab = initialTab,
            publicShareState = PublicShareTabState(selectedDocumentId = initialDocumentId),
            privateShareState = PrivateShareTabState(selectedDocumentId = initialDocumentId)
        )
    )
    val uiState: StateFlow<TransferUiState> = _uiState.asStateFlow()

    init {
        observeLocalDocuments()
    }

    fun selectTab(tab: TransferTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectPublicDocument(documentId: String) {
        _uiState.update {
            it.copy(publicShareState = it.publicShareState.copy(selectedDocumentId = documentId))
        }
    }

    fun updatePublicPassword(value: String) {
        _uiState.update {
            it.copy(publicShareState = it.publicShareState.copy(password = value))
        }
    }

    fun updatePublicExpireDays(value: String) {
        _uiState.update {
            it.copy(publicShareState = it.publicShareState.copy(expireDays = value.filter(Char::isDigit)))
        }
    }

    fun selectPrivateDocument(documentId: String) {
        _uiState.update {
            it.copy(privateShareState = it.privateShareState.copy(selectedDocumentId = documentId))
        }
    }

    fun updatePrivateEmail(value: String) {
        _uiState.update {
            it.copy(privateShareState = it.privateShareState.copy(email = value))
        }
    }

    fun updatePrivatePermission(permission: SharePermission) {
        _uiState.update {
            it.copy(privateShareState = it.privateShareState.copy(permission = permission))
        }
    }

    fun uploadFile(uri: Uri) {
        viewModelScope.launch {
            val fileName = getFileName(appContext, uri) ?: "scanlink_${System.currentTimeMillis()}"
            val tempFile = File(appContext.cacheDir, fileName)
            val itemId = UUID.randomUUID().toString()
            val initialItem = UploadDocumentItem(
                id = itemId,
                name = fileName,
                sizeLabel = "Preparing",
                thumbnailPath = null,
                progress = 0.08f,
                state = UploadState.Uploading
            )

            _uiState.update {
                it.copy(uploadState = it.uploadState.copy(items = listOf(initialItem) + it.uploadState.items))
            }

            val result = uploadUri(uri, tempFile, fileName) { progress ->
                updateUploadProgress(itemId, progress)
            }

            _uiState.update { state ->
                val updatedItems = state.uploadState.items.map { item ->
                    if (item.id == itemId) {
                        item.copy(
                            sizeLabel = formatFileSize(tempFile.length()),
                            thumbnailPath = tempFile.absolutePath,
                            progress = if (result) 1f else item.progress,
                            state = if (result) UploadState.Synced else UploadState.Failed
                        )
                    } else {
                        item
                    }
                }
                state.copy(
                    uploadState = state.uploadState.copy(items = updatedItems),
                    actionMessage = if (result) "Uploaded to cloud" else "Upload failed"
                )
            }
        }
    }

    fun retryUpload(itemId: String) {
        _uiState.update { state ->
            state.copy(
                uploadState = state.uploadState.copy(
                    items = state.uploadState.items.map {
                        if (it.id == itemId) it.copy(state = UploadState.Uploading, progress = 0.35f) else it
                    }
                ),
                actionMessage = "Retry queued"
            )
        }
    }

    fun cancelUpload(itemId: String) {
        _uiState.update { state ->
            state.copy(
                uploadState = state.uploadState.copy(
                    items = state.uploadState.items.filterNot { it.id == itemId && it.state == UploadState.Uploading }
                ),
                actionMessage = "Upload canceled"
            )
        }
    }

    fun generatePublicLink() {
        val state = _uiState.value
        val documentId = state.publicShareState.selectedDocumentId ?: return
        val document = state.documents.firstOrNull { it.id == documentId } ?: return
        val password = state.publicShareState.password.takeIf { it.isNotBlank() }
        val expireDays = state.publicShareState.expireDays.toIntOrNull()

        _uiState.update { it.copy(publicShareState = it.publicShareState.copy(isGenerating = true)) }

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                apiService.createPublicShareLink(
                    CreatePublicShareRequest(
                        documentId = documentId,
                        password = password,
                        expireInDays = expireDays
                    )
                ).execute()
            }.onSuccess { response ->
                val body = response.body()?.data
                if (!response.isSuccessful || body == null) {
                    _uiState.update {
                        it.copy(
                            publicShareState = it.publicShareState.copy(isGenerating = false),
                            actionMessage = "Could not generate link: ${response.code()}"
                        )
                    }
                    return@onSuccess
                }

                val link = PublicLinkItem(
                    id = body.hashToken,
                    documentId = documentId,
                    documentName = document.title,
                    url = body.shareUrl.ifBlank { "https://scanlink.app/share/${body.hashToken}" },
                    createdDate = formatDate(System.currentTimeMillis()),
                    expireDate = body.expiresAt?.let(::formatIsoDate),
                    passwordEnabled = body.hasPassword
                )

                _uiState.update { current ->
                    current.copy(
                        publicShareState = current.publicShareState.copy(
                            isGenerating = false,
                            password = "",
                            expireDays = "",
                            links = listOf(link) + current.publicShareState.links
                        ),
                        actionMessage = "Public link generated"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        publicShareState = it.publicShareState.copy(isGenerating = false),
                        actionMessage = "Could not generate link: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    fun disablePublicLink(linkId: String) {
        _uiState.update { state ->
            state.copy(
                publicShareState = state.publicShareState.copy(
                    links = state.publicShareState.links.map {
                        if (it.id == linkId) it.copy(isEnabled = false) else it
                    }
                ),
                actionMessage = "Public link disabled"
            )
        }
    }

    fun copyPublicLink(linkId: String) {
        val link = _uiState.value.publicShareState.links.firstOrNull { it.id == linkId } ?: return
        val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("ScanLink public link", link.url))
        _uiState.update { it.copy(actionMessage = "Link copied") }
    }

    fun sharePrivateAccess() {
        val state = _uiState.value
        val documentId = state.privateShareState.selectedDocumentId ?: return
        val document = state.documents.firstOrNull { it.id == documentId } ?: return
        val email = state.privateShareState.email.trim()
        if (email.isBlank()) {
            _uiState.update { it.copy(actionMessage = "Enter an email address") }
            return
        }

        _uiState.update { it.copy(privateShareState = it.privateShareState.copy(isSharing = true)) }

        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                apiService.grantPrivatePermission(
                    GrantPrivatePermissionRequest(
                        documentId = documentId,
                        shareToEmail = email,
                        role = state.privateShareState.permission.name.uppercase()
                    )
                ).execute()
            }.onSuccess { response ->
                val body = response.body()?.data
                if (!response.isSuccessful || body == null) {
                    _uiState.update {
                        it.copy(
                            privateShareState = it.privateShareState.copy(isSharing = false),
                            actionMessage = "Could not share access: ${response.code()}"
                        )
                    }
                    return@onSuccess
                }

                val role = body.role.let {
                    if (it.equals("EDITOR", ignoreCase = true)) SharePermission.Editor else SharePermission.Viewer
                }
                val sharedUser = SharedUserItem(
                    id = "${body.documentId}-${body.collaboratorEmail}",
                    documentId = body.documentId,
                    documentName = document.title,
                    email = body.collaboratorEmail,
                    permission = role,
                    sharedDate = formatDate(System.currentTimeMillis())
                )
                _uiState.update { current ->
                    current.copy(
                        privateShareState = current.privateShareState.copy(
                            isSharing = false,
                            email = "",
                            sharedUsers = listOf(sharedUser) + current.privateShareState.sharedUsers.filterNot {
                                it.documentId == documentId && it.email.equals(email, ignoreCase = true)
                            }
                        ),
                        actionMessage = "Private access shared"
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        privateShareState = it.privateShareState.copy(isSharing = false),
                        actionMessage = "Could not share access: ${error.localizedMessage}"
                    )
                }
            }
        }
    }

    fun removePrivateAccess(userId: String) {
        _uiState.update { state ->
            state.copy(
                privateShareState = state.privateShareState.copy(
                    sharedUsers = state.privateShareState.sharedUsers.filterNot { it.id == userId }
                ),
                actionMessage = "Access removed"
            )
        }
    }

    fun changePrivatePermission(userId: String, permission: SharePermission) {
        _uiState.update { state ->
            state.copy(
                privateShareState = state.privateShareState.copy(
                    sharedUsers = state.privateShareState.sharedUsers.map {
                        if (it.id == userId) it.copy(permission = permission) else it
                    }
                ),
                actionMessage = "Permission updated"
            )
        }
    }

    fun consumeActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    private fun observeLocalDocuments() {
        viewModelScope.launch {
            documentRepository.getDocumentsFlow().collect { docs ->
                val documentOptions = docs.map {
                    TransferDocumentOption(
                        id = it.id,
                        title = it.title,
                        sizeLabel = formatFileSize(it.fileSize)
                    )
                }
                val uploadItems = docs.map {
                    UploadDocumentItem(
                        id = it.id,
                        name = it.title,
                        sizeLabel = formatFileSize(it.fileSize),
                        thumbnailPath = it.thumbnailPath,
                        progress = if (it.isSynced) 1f else 0f,
                        state = if (it.isSynced) UploadState.Synced else UploadState.Failed
                    )
                }
                _uiState.update { current ->
                    current.copy(
                        documents = documentOptions,
                        uploadState = current.uploadState.copy(
                            items = (current.uploadState.items.filter { it.id !in docs.map(Document::id) } + uploadItems)
                                .distinctBy { it.id }
                        ),
                        publicShareState = current.publicShareState.copy(
                            selectedDocumentId = current.publicShareState.selectedDocumentId ?: documentOptions.firstOrNull()?.id
                        ),
                        privateShareState = current.privateShareState.copy(
                            selectedDocumentId = current.privateShareState.selectedDocumentId ?: documentOptions.firstOrNull()?.id
                        )
                    )
                }
            }
        }
    }

    private suspend fun uploadUri(uri: Uri, tempFile: File, fileName: String, onProgress: (Float) -> Unit): Boolean {
        val mimeType = appContext.contentResolver.getType(uri) ?: "application/octet-stream"
        return withContext(Dispatchers.IO) {
            runCatching {
                appContext.contentResolver.openInputStream(uri)?.use { input ->
                    tempFile.outputStream().use { output -> input.copyTo(output) }
                }
                onProgress(0.45f)

                val uploadFile = prepareUploadFile(tempFile, fileName, mimeType)
                val requestFile = uploadFile.asRequestBody(mimeType.toMediaTypeOrNull())
                val filePart = MultipartBody.Part.createFormData("file", uploadFile.name, requestFile)
                val titlePart = MultipartBody.Part.createFormData("title", uploadFile.name.substringBeforeLast("."))
                val textPart = MultipartBody.Part.createFormData("extractedText", "")
                val response = apiService.uploadDocument(filePart, titlePart, textPart).execute()
                onProgress(0.85f)

                if (response.isSuccessful && response.body()?.data != null) {
                    val serverDoc = response.body()!!.data!!
                    val now = System.currentTimeMillis()
                    val document = Document(
                        id = UUID.randomUUID().toString(),
                        ownerUid = serverDoc.ownerUid,
                        title = uploadFile.name,
                        storageUrl = serverDoc.storageUrl,
                        fileSize = uploadFile.length(),
                        extractedText = serverDoc.extractedText,
                        pdfPath = uploadFile.absolutePath,
                        createdAt = now,
                        updatedAt = now,
                        isSynced = true,
                        pageCount = 1,
                        mimeType = mimeType,
                        thumbnailPath = uploadFile.absolutePath,
                        lastModified = now
                    )
                    documentRepository.saveDocument(
                        document,
                        listOf(
                            Page(
                                id = UUID.randomUUID().toString(),
                                documentId = document.id,
                                pageNumber = 1,
                                imagePath = uploadFile.absolutePath,
                                ocrText = null,
                                createdAt = now
                            )
                        )
                    )
                    true
                } else {
                    false
                }
            }.getOrElse {
                it.printStackTrace()
                false
            }
        }
    }

    private fun prepareUploadFile(tempFile: File, fileName: String, mimeType: String): File {
        if (!mimeType.startsWith("image/", ignoreCase = true)) return tempFile
        val bitmap = BitmapFactory.decodeFile(tempFile.absolutePath) ?: return tempFile
        val maxDim = 900
        val scaled = if (bitmap.width > maxDim || bitmap.height > maxDim) {
            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
            val newWidth = if (ratio > 1) maxDim else (maxDim * ratio).toInt()
            val newHeight = if (ratio > 1) (maxDim / ratio).toInt() else maxDim
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
        val compressedFile = File(appContext.cacheDir, "compressed_${fileName.substringBeforeLast(".")}.jpg")
        compressedFile.outputStream().use { scaled.compress(Bitmap.CompressFormat.JPEG, 70, it) }
        return compressedFile
    }

    private fun updateUploadProgress(itemId: String, progress: Float) {
        _uiState.update { state ->
            state.copy(
                uploadState = state.uploadState.copy(
                    items = state.uploadState.items.map {
                        if (it.id == itemId) it.copy(progress = progress.coerceIn(0f, 1f)) else it
                    }
                )
            )
        }
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
            result = uri.path?.substringAfterLast('/')
        }
        return result
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }

    private fun formatIsoDate(value: String): String {
        return value.substringBefore("T").ifBlank { value }
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val group = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt().coerceIn(units.indices)
        return String.format(Locale.getDefault(), "%.1f %s", size / Math.pow(1024.0, group.toDouble()), units[group])
    }
}
