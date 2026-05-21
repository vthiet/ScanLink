package com.example.scanlink.di

import com.example.scanlink.features.file_sharing.data.remote.api.DocumentApiService
import com.example.scanlink.features.file_sharing.data.remote.interceptor.AuthInterceptor
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL =
        "https://api.scanlink.com/"

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {

        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        firebaseAuth: FirebaseAuth
    ): AuthInterceptor {

        return AuthInterceptor {

            firebaseAuth.currentUser
                ?.getIdToken(false)
                ?.result
                ?.token
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        client: OkHttpClient
    ): Retrofit {

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(
                GsonConverterFactory.create(
                    Gson()
                )
            )
            .build()
    }

    @Provides
    @Singleton
    fun provideDocumentApiService(
        retrofit: Retrofit
    ): DocumentApiService {

        return retrofit.create(
            DocumentApiService::class.java
        )
    }
}