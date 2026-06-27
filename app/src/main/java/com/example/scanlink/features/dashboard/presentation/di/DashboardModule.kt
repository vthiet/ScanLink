package com.example.scanlink.features.dashboard.presentation.di

import com.example.scanlink.features.dashboard.data.repositories.DashboardPreferencesRepositoryImpl
import com.example.scanlink.features.dashboard.domain.repositories.IDashboardPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DashboardModule {

    @Provides
    @Singleton
    fun provideDashboardPreferencesRepository(
        repositoryImpl: DashboardPreferencesRepositoryImpl
    ): IDashboardPreferencesRepository {
        return repositoryImpl
    }
}