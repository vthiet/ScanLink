package com.example.scanlink.core.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.scanlink.features.file_sharing.data.worker.UploadWorker
import java.util.concurrent.TimeUnit

object SyncManager {

    fun startSyncWorker(context: Context) {

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request =
            PeriodicWorkRequestBuilder<UploadWorker>(
                15,
                TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "upload_sync_worker",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
    }
}