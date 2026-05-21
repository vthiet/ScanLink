package com.example.scanlink.di

import android.content.Context
import androidx.room.Room
import com.example.scanlink.features.file_sharing.data.local.dao.DocumentDao
import com.example.scanlink.features.file_sharing.data.local.database.AppDatabase
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
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {

        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "scanlink_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideDocumentDao(
        database: AppDatabase
    ): DocumentDao {

        return database.documentDao()
    }
}