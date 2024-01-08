package jinproject.stepwalk.home.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import jinproject.stepwalk.home.service.StepService

@HiltWorker
class RestartServiceWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
): Worker(context, workerParams) {
    override fun doWork(): Result =
        kotlin.runCatching {
            Log.d("test","done")
            context.startService(Intent(context, StepService::class.java))
            Result.success()
        }.getOrElse { e ->
            Result.failure(Data.Builder()
                .putString("error",e.message.toString())
                .build()
            )
        }
}