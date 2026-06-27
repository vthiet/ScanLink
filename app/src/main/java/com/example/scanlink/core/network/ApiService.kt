package com.example.scanlink.core.network

import com.example.scanlink.core.network.models.CreatePublicShareRequest
import com.example.scanlink.core.network.models.DocumentResponse
import com.example.scanlink.core.network.models.GrantPrivatePermissionRequest
import com.example.scanlink.core.network.models.PageResponse
import com.example.scanlink.core.network.models.PrivatePermissionResponse
import com.example.scanlink.core.network.models.ShareLinkResponse
import com.example.scanlink.core.network.models.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("api/v1/auth/register")
    fun registerOrSyncUser(): Call<BaseResponse<UserResponse>>

    @POST("api/v1/auth/login")
    fun loginCheck(): Call<BaseResponse<UserResponse>>

    @Multipart
    @POST("api/v1/documents")
    fun uploadDocument(
        @Part file: MultipartBody.Part,
        @Part("title") title: RequestBody,
        @Part("extractedText") extractedText: RequestBody? = null
    ): Call<BaseResponse<DocumentResponse>>

    @GET("api/v1/documents")
    fun getPersonalDocuments(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20,
        @Query("sortBy") sortBy: String = "createdAt"
    ): Call<BaseResponse<PageResponse<DocumentResponse>>>

    @GET("api/v1/documents/{id}")
    fun getDocumentDetail(
        @Path("id") id: String
    ): Call<BaseResponse<DocumentResponse>>

    @DELETE("api/v1/documents/{id}")
    fun deleteDocument(
        @Path("id") id: String
    ): Call<BaseResponse<Unit>>

    @POST("api/v1/shares/public")
    fun createPublicShareLink(
        @Body request: CreatePublicShareRequest
    ): Call<BaseResponse<ShareLinkResponse>>

    @POST("api/v1/shares/private")
    fun grantPrivatePermission(
        @Body request: GrantPrivatePermissionRequest
    ): Call<BaseResponse<PrivatePermissionResponse>>

    @Streaming
    @GET("api/v1/shares/public/{hash_token}")
    fun downloadPublicFile(
        @Path("hash_token") hashToken: String,
        @Query("password") password: String? = null
    ): Call<ResponseBody>
}