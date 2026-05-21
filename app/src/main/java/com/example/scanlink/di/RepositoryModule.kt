package com.example.scanlink.di

import com.example.scanlink.features.file_sharing.data.local.dao.DocumentDao
import com.example.scanlink.features.file_sharing.data.remote.api.DocumentApiService
import com.example.scanlink.features.file_sharing.data.repository.FileSharingRepositoryImpl
import com.example.scanlink.features.file_sharing.domain.repository.FileSharingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideFileSharingRepository(
        apiService: DocumentApiService,
        documentDao: DocumentDao
    ): FileSharingRepository {

        return FileSharingRepositoryImpl(
            apiService,
            documentDao
        )
    }
}