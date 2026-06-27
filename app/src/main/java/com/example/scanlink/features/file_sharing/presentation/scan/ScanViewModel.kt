package com.example.scanlink.features.file_sharing.presentation.scan

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.core.network.ApiService
import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.entities.Page
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

data class ScanUiState(
    val isLoading: Boolean = false,
    val isPasswordPromptVisible: Boolean = false,
    val passwordValue: String = "",
    val activeHashToken: String? = null,
    val errorMessage: String? = null,
    val successDocumentId: String? = null
)

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val apiService: ApiService,
    private val documentRepository: DocumentRepository,
    @ApplicationContext private val appContext: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScanUiState())
    val uiState: StateFlow<ScanUiState> = _uiState.asStateFlow()

    fun processScannedToken(token: String) {
        // Trích xuất hash token từ link nếu nó là full URL (ví dụ: http://scanlink.com/share/abcxyz)
        val hashToken = if (token.contains("/")) {
            token.substringAfterLast("/")
        } else {
            token
        }.trim()

        if (hashToken.isBlank()) return

        _uiState.update { it.copy(activeHashToken = hashToken, errorMessage = null) }
        downloadFileInternal(hashToken, null)
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(passwordValue = password) }
    }

    fun confirmPassword() {
        val state = _uiState.value
        val hashToken = state.activeHashToken ?: return
        val password = state.passwordValue

        _uiState.update { it.copy(isPasswordPromptVisible = false) }
        downloadFileInternal(hashToken, password)
    }

    fun dismissPasswordPrompt() {
        _uiState.update { it.copy(isPasswordPromptVisible = false, activeHashToken = null) }
    }

    private fun downloadFileInternal(hashToken: String, password: String?) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.downloadPublicFile(hashToken, password).execute()

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val fileName = "Share_${hashToken.take(6)}_${System.currentTimeMillis()}.pdf"
                    val file = File(appContext.cacheDir, fileName)

                    body.byteStream().use { inputStream ->
                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }

                    val now = System.currentTimeMillis()
                    val docId = UUID.randomUUID().toString()
                    val newDoc = Document(
                        id = docId,
                        ownerUid = null,
                        title = fileName,
                        storageUrl = null,
                        fileSize = file.length(),
                        extractedText = "Tài liệu tải xuống từ mã chia sẻ $hashToken",
                        pdfPath = file.absolutePath,
                        createdAt = now,
                        updatedAt = now,
                        isSynced = true,
                        pageCount = 1,
                        mimeType = "application/pdf",
                        thumbnailPath = null,
                        lastModified = now
                    )
                    val page = Page(
                        id = UUID.randomUUID().toString(),
                        documentId = docId,
                        pageNumber = 1,
                        imagePath = file.absolutePath,
                        ocrText = null,
                        createdAt = now
                    )
                    documentRepository.saveDocument(newDoc, listOf(page))

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successDocumentId = docId,
                            activeHashToken = null
                        )
                    }
                } else {
                    if (response.code() == 401 || response.code() == 403) {
                        // Cần mật khẩu bảo vệ
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isPasswordPromptVisible = true,
                                passwordValue = ""
                            )
                        }
                    } else {
                        throw Exception("Lỗi server: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Tải file thất bại: ${e.localizedMessage ?: "Lỗi kết nối"}",
                        activeHashToken = null
                    )
                }
            }
        }
    }

    fun consumeSuccessState() {
        _uiState.update { it.copy(successDocumentId = null) }
    }
}
