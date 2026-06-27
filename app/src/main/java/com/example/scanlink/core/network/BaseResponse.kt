package com.example.scanlink.core.network

data class BaseResponse<T>(
    val status: String,
    val message: String,
    val data: T?
)