package jinproject.stepwalk.home.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.AndroidEntryPoint
import jinproject.stepwalk.domain.usecase.SetStepUseCase

@AndroidEntryPoint
internal class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        val lastStep = p1?.getLongExtra("lastStep", 0L) ?: 0L

        OneTimeWorkRequestBuilder<InsertLastStepWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(Data.Builder().putLong("lastStep",lastStep).build())
            .build()

    }
}

@HiltWorker
internal class InsertLastStepWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val setStepUseCase: SetStepUseCase
):CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        kotlin.runCatching {
            val lastStep = inputData.getLong("lastStep", 0L)
            setStepUseCase.setLastStep(lastStep)
        }.onFailure {
            return Result.failure()
        }

        return Result.success()
    }
}