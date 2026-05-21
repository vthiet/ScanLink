package com.example.scanlink.features.file_sharing.data.repository

import com.example.scanlink.features.file_sharing.data.local.dao.DocumentDao
import com.example.scanlink.features.file_sharing.data.mapper.toDomain
import com.example.scanlink.features.file_sharing.data.mapper.toEntity
import com.example.scanlink.features.file_sharing.data.remote.api.DocumentApiService
import com.example.scanlink.features.file_sharing.data.remote.dto.ShareRequest
import com.example.scanlink.features.file_sharing.domain.model.Document
import com.example.scanlink.features.file_sharing.domain.repository.FileSharingRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class FileSharingRepositoryImpl(
    private val apiService: DocumentApiService,
    private val documentDao: DocumentDao
) : FileSharingRepository {

    override suspend fun getLocalDocuments():
            List<Document> {

        return documentDao
            .getAllDocuments()
            .map {
                it.toDomain()
            }
    }

    override suspend fun saveDocument(
        document: Document
    ) {

        documentDao.insertDocument(
            document.toEntity()
        )
    }

    override suspend fun getPendingUploads():
            List<Document> {

        return documentDao
            .getPendingUploads()
            .map {
                it.toDomain()
            }
    }

    override suspend fun uploadDocument(
        file: File
    ): Result<Document> {

        return try {

            val requestFile =
                file.asRequestBody(
                    "application/pdf".toMediaType()
                )

            val multipartBody =
                MultipartBody.Part.createFormData(
                    "file",
                    file.name,
                    requestFile
                )

            val response =
                apiService.uploadDocument(
                    multipartBody
                )

            val document = Document(
                id = response.id,
                title = response.title,
                storageUrl = response.storageUrl,
                localPath = file.absolutePath,
                fileSize = file.length(),
                extractedText = null,
                isUploaded = true,
                createdAt = System.currentTimeMillis()
            )

            documentDao.insertDocument(
                document.toEntity()
            )

            Result.success(document)

        } catch (e: Exception) {

            val offlineDocument = Document(
                id = java.util.UUID.randomUUID().toString(),
                title = file.name,
                storageUrl = null,
                localPath = file.absolutePath,
                fileSize = file.length(),
                extractedText = null,
                isUploaded = false,
                createdAt = System.currentTimeMillis()
            )

            documentDao.insertDocument(
                offlineDocument.toEntity()
            )

            Result.success(offlineDocument)
        }
    }

    override suspend fun createShareLink(
        documentId: String
    ): Result<String> {

        return try {

            val response =
                apiService.createShareLink(
                    ShareRequest(documentId)
                )

            Result.success(response.shareUrl)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

//   Lấy pending docs
//            ↓
//    Upload từng file
//    ↓
//    Update Room nếu thành công

    override suspend fun syncPendingDocuments() {

        val pendingDocuments =
            documentDao.getPendingUploads()

        pendingDocuments.forEach { entity ->

            try {

                val file =
                    File(
                        entity.localPath ?: return@forEach
                    )

                if (!file.exists()) {
                    return@forEach
                }

                val requestFile =
                    file.asRequestBody(
                        "application/pdf".toMediaType()
                    )

                val multipartBody =
                    MultipartBody.Part.createFormData(
                        "file",
                        file.name,
                        requestFile
                    )

                val response =
                    apiService.uploadDocument(
                        multipartBody
                    )

                documentDao.markAsUploaded(
                    entity.id,
                    response.storageUrl
                )

            } catch (_: Exception) {

            }
        }
    }
}
