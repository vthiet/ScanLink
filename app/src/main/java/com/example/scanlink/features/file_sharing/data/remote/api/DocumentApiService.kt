package com.example.scanlink.features.file_sharing.data.remote.api

import com.example.scanlink.features.file_sharing.data.remote.dto.ShareRequest
import com.example.scanlink.features.file_sharing.data.remote.response.ShareResponse
import com.example.scanlink.features.file_sharing.data.remote.response.UploadResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface DocumentApiService {

    @Multipart
    @POST("/api/v1/documents")
    suspend fun uploadDocument(

        @Part file: MultipartBody.Part

    ): UploadResponse

    @POST("/api/v1/shares/public")
    suspend fun createShareLink(

        @Body request: ShareRequest

    ): ShareResponse
}