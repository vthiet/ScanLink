package com.example.scanlink.features.document_scanner.domain.usecases

import com.example.scanlink.core.network.ApiService
import com.example.scanlink.features.document_scanner.domain.entities.Document
import com.example.scanlink.features.document_scanner.domain.repositories.DocumentRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.UUID
import javax.inject.Inject

class SyncDocumentUseCase @Inject constructor(
    private val apiService: ApiService,
    private val documentRepository: DocumentRepository
) {
    suspend operator fun invoke(document: Document): Document {
        if (document.isSynced && !document.storageUrl.isNullOrBlank()) {
            return document
        }

        val pdfPath = document.pdfPath
        if (pdfPath.isNullOrBlank()) {
            throw Exception("Không tìm thấy đường dẫn tệp PDF cục bộ để đồng bộ.")
        }

        val file = File(pdfPath)
        if (!file.exists()) {
            throw Exception("Tệp PDF cục bộ không tồn tại ở đường dẫn: $pdfPath")
        }

        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val cleanTitle = file.name.substringBeforeLast(".")
        val titlePart = MultipartBody.Part.createFormData("title", cleanTitle)
        val textPart = MultipartBody.Part.createFormData("extractedText", document.extractedText.orEmpty())

        val response = apiService.uploadDocument(filePart, titlePart, textPart).execute()
        if (response.isSuccessful && response.body()?.data != null) {
            val serverDoc = response.body()!!.data!!
            val now = System.currentTimeMillis()

            // Xóa document cũ với ID cục bộ
            documentRepository.deleteDocument(document.id)

            // Lưu document mới với ID từ server
            val syncedId = serverDoc.id ?: throw Exception("Server không trả về ID tài liệu.")
            val syncedDoc = document.copy(
                id = syncedId,
                ownerUid = serverDoc.ownerUid,
                storageUrl = serverDoc.storageUrl,
                isSynced = true,
                updatedAt = now,
                lastModified = now,
                pages = document.pages.map { page ->
                    page.copy(
                        id = UUID.randomUUID().toString(),
                        documentId = syncedId,
                        createdAt = now
                    )
                }
            )
            documentRepository.saveDocument(syncedDoc, syncedDoc.pages)
            return syncedDoc
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception("Đồng bộ lên server thất bại: ${response.code()} $errorBody")
        }
    }
}
