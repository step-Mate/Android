package jinproject.stepwalk.home.worker

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.service.StepService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltWorker
class StepInsertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val healthConnector: HealthConnector
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        withContext(Dispatchers.IO) {
            healthConnector.insertSteps(
                step = inputData.getLong("distance",0L),
                startTime = Instant.ofEpochSecond(inputData.getLong("start",0L)),
                endTime = Instant.ofEpochSecond(inputData.getLong("end",0L))
            )
        }

        return Result.success()
    }
}