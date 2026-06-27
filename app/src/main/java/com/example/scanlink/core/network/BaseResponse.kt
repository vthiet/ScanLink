package com.example.scanlink.core.network

import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(
    val status: String,
    val message: String,
    @SerializedName(value = "data", alternate = ["result"])
    val data: T?
)