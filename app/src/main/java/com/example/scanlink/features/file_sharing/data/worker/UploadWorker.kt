package com.example.scanlink.features.file_sharing.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.scanlink.features.file_sharing.domain.repository.FileSharingRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class UploadWorker @AssistedInject constructor(

    @Assisted
    context: Context,

    @Assisted
    params: WorkerParameters,

    private val repository:
    FileSharingRepository

) : CoroutineWorker(
    context,
    params
) {

    override suspend fun doWork():
            Result {

        return try {

            repository
                .syncPendingDocuments()

            Result.success()

        } catch (e: Exception) {

            Result.retry()
        }
    }
}