package com.example.scanlink.core.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val currentUser = firebaseAuth.currentUser

        if (currentUser == null) {
            return chain.proceed(originalRequest)
        }

        return try {
            val tokenResult = Tasks.await(currentUser.getIdToken(false))
            val token = tokenResult.token
            if (token != null) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(originalRequest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            chain.proceed(originalRequest)
        }
    }
}
