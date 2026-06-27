package com.example.scanlink.features.dashboard.domain.repositories

import com.example.scanlink.core.network.models.DocumentResponse
import com.example.scanlink.core.network.models.PageResponse

interface IDashboardRepository {
    suspend fun getPersonalDocuments(page: Int, size: Int): Result<PageResponse<DocumentResponse>>
}
