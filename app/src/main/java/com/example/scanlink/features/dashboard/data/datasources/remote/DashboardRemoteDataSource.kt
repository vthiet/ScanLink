package com.example.scanlink.features.dashboard.data.datasources.remote

import com.example.scanlink.core.network.ApiService
import javax.inject.Inject

class DashboardRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) {
    fun getPersonalDocuments(page: Int, size: Int) =
        apiService.getPersonalDocuments(page, size)
}
