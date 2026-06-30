package com.example.scanlink.core.di

import android.content.Context
import com.example.scanlink.features.document_scanner.data.engine.ONNXEmbeddingEngine
import com.example.scanlink.features.document_scanner.data.engine.ONNXTokenizer
import com.example.scanlink.features.document_scanner.data.local.database.dao.DocumentChunkDao
import com.example.scanlink.features.document_scanner.data.local.database.dao.DocumentDao
import com.example.scanlink.features.document_scanner.data.repositories.SemanticSearchRepositoryImpl
import com.example.scanlink.features.document_scanner.domain.repositories.ISemanticSearchRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SemanticSearchModule {

    @Provides
    @Singleton
    fun provideONNXTokenizer(@ApplicationContext context: Context): ONNXTokenizer {
        return ONNXTokenizer(context)
    }

    @Provides
    @Singleton
    fun provideONNXEmbeddingEngine(
        @ApplicationContext context: Context,
        tokenizer: ONNXTokenizer
    ): ONNXEmbeddingEngine {
        return ONNXEmbeddingEngine(context, tokenizer)
    }

    @Provides
    @Singleton
    fun provideSemanticSearchRepository(
        chunkDao: DocumentChunkDao,
        documentDao: DocumentDao,
        engine: ONNXEmbeddingEngine
    ): ISemanticSearchRepository {
        return SemanticSearchRepositoryImpl(chunkDao, documentDao, engine)
    }
}
