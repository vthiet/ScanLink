package com.example.scanlink.features.authentication.data.datasources.remote.api

import com.example.scanlink.features.authentication.data.datasources.remote.dto.ApiResponse
import com.example.scanlink.features.authentication.data.datasources.remote.dto.RegisterResponse
import retrofit2.http.Header
import retrofit2.http.POST

interface IAuthApiService {

    @POST("/api/v1/auth/register")
    suspend fun registerWithEmailAndPassword(
        @Header("Authorization") authorization: String
    ): ApiResponse<RegisterResponse>

    @POST("/api/v1/auth/login")
    suspend fun loginWithEmail(
        @Header("Authorization") authorization: String
    ): ApiResponse<RegisterResponse>


    @POST("/api/v1/auth/refresh")
    suspend fun refreshToken(
        @Header("Authorization") refreshToken: String
    ): RegisterResponse

}