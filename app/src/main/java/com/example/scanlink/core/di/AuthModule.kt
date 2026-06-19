package com.example.scanlink.core.di

import com.example.scanlink.features.authentication.data.datasources.FirebaseAuthDataSource
import com.example.scanlink.features.authentication.data.remote.AuthRemoteDataSource
import com.example.scanlink.features.authentication.data.repositories.AuthRepositoryImpl
import com.example.scanlink.features.authentication.domain.repositories.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    private const val BACKEND_BASE_URL = "http://10.0.2.2:8080"

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
        return AuthRemoteDataSource(BACKEND_BASE_URL)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository {
        return authRepositoryImpl
    }
}
