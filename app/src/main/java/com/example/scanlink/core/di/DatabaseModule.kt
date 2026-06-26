package com.example.scanlink.core.di

import android.content.Context
import androidx.room.Room
import com.example.scanlink.features.document_scanner.data.local.database.AppDatabase
import com.example.scanlink.features.document_scanner.data.local.database.dao.DocumentDao
import com.example.scanlink.features.document_scanner.data.repositories.DocumentLocalRepositoryImpl
import com.example.scanlink.features.document_scanner.domain.repositories.IDocumentLocalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "scanlink_database"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideDocumentDao(database: AppDatabase): DocumentDao {
        return database.documentDao()
    }

    @Provides
    @Singleton
    fun provideDocumentLocalRepository(
        documentDao: DocumentDao
    ): IDocumentLocalRepository {
        return DocumentLocalRepositoryImpl(documentDao)
    }
}
