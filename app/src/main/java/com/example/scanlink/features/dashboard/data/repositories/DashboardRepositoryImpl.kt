package com.example.scanlink.features.dashboard.data.repositories

import com.example.scanlink.core.network.models.DocumentResponse
import com.example.scanlink.core.network.models.PageResponse
import com.example.scanlink.features.dashboard.data.datasources.remote.DashboardRemoteDataSource
import com.example.scanlink.features.dashboard.domain.repositories.IDashboardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DashboardRepositoryImpl @Inject constructor(
    private val remoteDataSource: DashboardRemoteDataSource
) : IDashboardRepository {

    override suspend fun getPersonalDocuments(page: Int, size: Int): Result<PageResponse<DocumentResponse>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = remoteDataSource.getPersonalDocuments(page, size).execute()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && body.data != null) {
                        body.data
                    } else {
                        throw Exception(body?.message ?: "Lấy dữ liệu thất bại")
                    }
                } else {
                    throw Exception("Lỗi kết nối server: ${response.code()}")
                }
            }
        }
}
