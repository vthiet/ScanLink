package com.example.scanlink.features.file_sharing.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenProvider: () -> String?
) : Interceptor {

    override fun intercept(
        chain: Interceptor.Chain
    ): Response {

        val token = tokenProvider()

        val request = chain.request()
            .newBuilder()
            .apply {

                token?.let {
                    addHeader(
                        "Authorization",
                        "Bearer $it"
                    )
                }
            }
            .build()

        return chain.proceed(request)
    }
}