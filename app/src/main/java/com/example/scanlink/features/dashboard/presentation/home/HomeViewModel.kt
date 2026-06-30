package com.example.scanlink.features.dashboard.presentation.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.core.network.models.DocumentResponse
import com.example.scanlink.core.ui.model.FileType
import com.example.scanlink.core.ui.model.RecentFile
import com.example.scanlink.features.dashboard.domain.usecases.GetDashboardDataUseCase
import com.example.scanlink.features.document_scanner.domain.usecases.CreateMultiImagePdfUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val documents: List<RecentFile> = emptyList(),
    val errorMessage: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val createMultiImagePdfUseCase: CreateMultiImagePdfUseCase,
    private val getDashboardDataUseCase: GetDashboardDataUseCase
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
                            errorMessage = error.localizedMessage ?: "Lỗi tải tài liệu từ server"
                        )
                    }
                }
        }
    }

    fun createPdfFromUris(context: Context, uris: List<Uri>) {
        if (uris.isEmpty()) return

        viewModelScope.launch {
            try {
                // 1. Chuyển đổi URIs thành Bitmaps (chạy trên IO thread)
                val bitmaps = withContext(Dispatchers.IO) {
                    uris.mapNotNull { uri ->
                        context.contentResolver.openInputStream(uri)?.use {
                            BitmapFactory.decodeStream(it)
                        }
                    }
                }

                if (bitmaps.isNotEmpty()) {
                    // 2. Gọi Use Case để tạo PDF
                    val pdfFile = createMultiImagePdfUseCase(bitmaps, "ScanLink_Imported_${System.currentTimeMillis()}")

                    if (pdfFile != null) {
                        Toast.makeText(context, "Đã tạo PDF thành công tại: ${pdfFile.name}", Toast.LENGTH_LONG).show()
                        // Reload documents list after creating a new PDF
                        loadDocuments()
                    } else {
                        Toast.makeText(context, "Lỗi khi tạo PDF", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

private fun DocumentResponse.toRecentFile(): RecentFile {
    val fileType = when {
        title.endsWith(".pdf", ignoreCase = true) -> FileType.PDF
        title.endsWith(".docx", ignoreCase = true) -> FileType.DOCX
        title.endsWith(".jpg", ignoreCase = true) || title.endsWith(".jpeg", ignoreCase = true) || title.endsWith(".png", ignoreCase = true) -> FileType.JPG
        else -> FileType.OTHER
    }

    val sizeLabel = formatFileSize(fileSize)

    val dateStr = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
        val parsedDate = inputFormat.parse(createdAt) ?: Date()
        outputFormat.format(parsedDate)
    } catch (_: Exception) {
        createdAt.take(10).replace("-", "/")
    }

    return RecentFile(
        id = id,
        name = title,
        type = fileType,
        createdAt = dateStr,
        sizeLabel = sizeLabel,
        statusText = "Đồng bộ",
        statusColor = Color(0xFF00CFA4)
    )
}

private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.toDouble())).toInt()
    val group = if (digitGroups in units.indices) digitGroups else 0
    return String.format(Locale.getDefault(), "%.1f %s", size / Math.pow(1024.toDouble(), group.toDouble()), units[group])
}
