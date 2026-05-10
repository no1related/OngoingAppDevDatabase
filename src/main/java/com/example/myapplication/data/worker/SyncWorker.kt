package com.example.myapplication.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.myapplication.data.database.UserDatabase
import com.example.myapplication.data.remote.FirestoreDataSource
import com.example.myapplication.data.repository.SyncRepository
import java.util.concurrent.TimeUnit

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        // Retrieve branchId passed in when the worker was scheduled
        val branchId = inputData.getInt(KEY_BRANCH_ID, -1)
        if (branchId == -1) return Result.failure()

        return try {
            val database   = UserDatabase.getInstance(applicationContext)
            val firestore  = FirestoreDataSource()
            val repository = SyncRepository(database, firestore)

            repository.syncAll(branchId)

            Result.success()

        } catch (e: Exception) {
            // Retry up to 3 times before giving up
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    companion object {
        const val KEY_BRANCH_ID = "branch_id"

        /**
         * Schedule a periodic sync every 30 minutes.
         * Call this once from your Application class or MainActivity.
         *
         * Usage:
         *   SyncWorker.schedule(context, branchId = 1)
         */
        fun schedule(context: Context, branchId: Int) {
            val inputData = workDataOf(KEY_BRANCH_ID to branchId)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // only run when online
                .build()

            val request = PeriodicWorkRequestBuilder<SyncWorker>(30, TimeUnit.MINUTES)
                .setInputData(inputData)
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .build()

            // KEEP — won't replace an already-running sync job
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "pos_sync_branch_$branchId",
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        /**
         * Trigger a one-time immediate sync (e.g. after completing an order).
         *
         * Usage:
         *   SyncWorker.runNow(context, branchId = 1)
         */
        fun runNow(context: Context, branchId: Int) {
            val inputData = workDataOf(KEY_BRANCH_ID to branchId)

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = OneTimeWorkRequestBuilder<SyncWorker>()
                .setInputData(inputData)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}