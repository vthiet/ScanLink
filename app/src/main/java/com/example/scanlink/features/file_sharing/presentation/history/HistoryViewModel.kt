package com.example.scanlink.features.file_sharing.presentation.history

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.scanlink.core.ui.model.FileType
import com.example.scanlink.core.ui.model.RecentFile
import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.repositories.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class HistoryUiState(
    val groupedFiles: List<DateGroup> = emptyList()
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val documentRepository: DocumentRepository,
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        val currentUid = firebaseAuth.currentUser?.uid
        viewModelScope.launch {
            if (currentUid != null) {
                documentRepository.associateGuestDocuments(currentUid)
            }
            documentRepository.getDocumentsFlow(currentUid).collect { docs ->
                val grouped = groupDocumentsByDate(docs)
                _uiState.update { it.copy(groupedFiles = grouped) }
            }
        }
    }

    private fun groupDocumentsByDate(documents: List<Document>): List<DateGroup> {
        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val todayStr = formatter.format(Date())
        val yesterdayStr = formatter.format(Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000))

        val groups = documents.groupBy { doc ->
            val dateStr = formatter.format(Date(doc.createdAt))
            when (dateStr) {
                todayStr -> "HÔM NAY — $dateStr"
                yesterdayStr -> "HÔM QUA — $dateStr"
                else -> "TRƯỚC ĐÓ — $dateStr"
            }
        }

        return groups.map { (title, docsInGroup) ->
            DateGroup(
                title = title,
                files = docsInGroup.map { doc ->
                    val fileType = when {
                        doc.title.endsWith(".pdf", ignoreCase = true) -> FileType.PDF
                        doc.title.endsWith(".docx", ignoreCase = true) -> FileType.DOCX
                        doc.title.endsWith(".jpg", ignoreCase = true) || doc.title.endsWith(".jpeg", ignoreCase = true) || doc.title.endsWith(".png", ignoreCase = true) -> FileType.JPG
                        else -> FileType.OTHER
                    }

                    val statusText = when {
                        doc.isSynced -> "Tải lên"
                        doc.extractedText != null -> "Quét OCR"
                        else -> "Bản nháp"
                    }

                    val statusColor = when {
                        doc.isSynced -> Color(0xFF00CFA4)
                        doc.extractedText != null -> Color(0xFF9B59B6)
                        else -> Color(0xFF3498DB)
                    }

                    RecentFile(
                        id = doc.id,
                        name = doc.title,
                        type = fileType,
                        createdAt = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(doc.createdAt)),
                        sizeLabel = formatFileSize(doc.fileSize),
                        statusText = statusText,
                        statusColor = statusColor
                    )
                }
            )
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
