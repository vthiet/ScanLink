package com.example.scanlink.core.network

import com.example.scanlink.core.network.models.DocumentResponse
import com.example.scanlink.core.network.models.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @GET("api/v1/documents/{id}")
    fun getDocumentDetail(@Path("id") id: String): Call<BaseResponse<DocumentResponse>>
}