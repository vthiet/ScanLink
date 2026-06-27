package com.example.scanlink.features.dashboard.domain.usecases

import com.example.scanlink.core.network.models.DocumentResponse
import com.example.scanlink.core.network.models.PageResponse
import com.example.scanlink.features.dashboard.domain.repositories.IDashboardRepository
import javax.inject.Inject

class GetDashboardDataUseCase @Inject constructor(
    private val dashboardRepository: IDashboardRepository
) {
    suspend operator fun invoke(page: Int = 0, size: Int = 20): Result<PageResponse<DocumentResponse>> {
        return dashboardRepository.getPersonalDocuments(page, size)
    }
}