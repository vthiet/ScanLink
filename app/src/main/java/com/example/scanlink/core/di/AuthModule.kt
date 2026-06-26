package com.example.scanlink.core.di

import com.example.scanlink.BuildConfig
import com.example.scanlink.features.authentication.data.datasources.remote.AuthRemoteDataSource
import com.example.scanlink.features.authentication.data.datasources.remote.FirebaseAuthDataSource
import com.example.scanlink.features.authentication.data.datasources.remote.api.IAuthApiService
import com.example.scanlink.features.authentication.data.repositories.AuthenticationRepositoryImpl
import com.example.scanlink.features.authentication.domain.repositories.IAuthenticationRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuthDataSource(firebaseAuth: FirebaseAuth): FirebaseAuthDataSource {
        return FirebaseAuthDataSource(firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideAuthRemoteDataSource(): AuthRemoteDataSource {
        return AuthRemoteDataSource(BuildConfig.BASE_URL)
    }

    @Provides
    @Singleton
    @AuthRetrofit
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@AuthRetrofit retrofit: Retrofit): IAuthApiService {
        return retrofit.create(IAuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
            authRepositoryImpl: AuthenticationRepositoryImpl
    ): IAuthenticationRepository {
        return authRepositoryImpl
    }
}
