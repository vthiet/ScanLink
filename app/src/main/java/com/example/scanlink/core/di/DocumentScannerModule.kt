package com.example.scanlink.core.di

import com.example.scanlink.features.document_scanner.data.repositories.PreviewImageRepositoryImpl
import com.example.scanlink.features.document_scanner.data.repositories.ScanProcessingRepositoryImpl
import com.example.scanlink.features.document_scanner.domain.repositories.IPreviewImageRepository
import com.example.scanlink.features.document_scanner.domain.repositories.IScanProcessingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DocumentScannerModule {
    @Binds
    @Singleton
    abstract fun bindScanProcessingRepository(
        repository: ScanProcessingRepositoryImpl
    ): IScanProcessingRepository

    @Binds
    @Singleton
    abstract fun bindPreviewImageRepository(
        repository: PreviewImageRepositoryImpl
    ): IPreviewImageRepository
}
