package jinproject.stepwalk.home.worker

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import jinproject.stepwalk.home.service.StepService

internal class StartServiceWorker(private val context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams) {
    override fun doWork(): Result {

        context.startForegroundService(Intent(context, StepService::class.java).apply {
            putExtra("insertStep",true)
        })

        return Result.success()
    }
}