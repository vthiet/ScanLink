package com.example.scanlink.di

import com.example.scanlink.features.file_sharing.domain.repository.FileSharingRepository
import com.example.scanlink.features.file_sharing.domain.usecase.GetDocumentsUseCase
import com.example.scanlink.features.file_sharing.domain.usecase.UploadDocumentUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    fun provideUploadDocumentUseCase(
        repository: FileSharingRepository
    ): UploadDocumentUseCase {

        return UploadDocumentUseCase(
            repository
        )
    }
    @Provides
    fun provideGetDocumentsUseCase(
        repository: FileSharingRepository
    ): GetDocumentsUseCase {

        return GetDocumentsUseCase(
            repository
        )
    }
}