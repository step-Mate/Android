package com.stepmate.home.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.stepmate.domain.usecase.mission.UpdateMissionUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
internal class MissionUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val updateMissionUseCases: UpdateMissionUseCases
) : CoroutineWorker(context, workerParams) {
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        Log.d("test", "error has occurred : ${t.message}")
    }

    override suspend fun doWork(): Result {
        val walked = inputData.getLong("walk", 0)
        withContext(Dispatchers.IO + coroutineExceptionHandler) {
            updateMissionUseCases(walked.toInt())
        }
        return Result.success()
    }
}