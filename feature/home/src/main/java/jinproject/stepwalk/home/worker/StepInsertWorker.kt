package jinproject.stepwalk.home.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import jinproject.stepwalk.domain.usecase.SetStepUseCase
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.service.StepSensorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

@HiltWorker
class StepInsertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val healthConnector: HealthConnector,
    private val setStepUseCase: SetStepUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val distance = inputData.getLong(StepSensorViewModel.Key.DISTANCE.value, 0L)

        if(distance == 0L)
            return Result.failure(Data.Builder().putString("fail","걸음수는 0이 될 수 없습니다.").build())

        withContext(Dispatchers.IO) {
            healthConnector.insertSteps(
                step = distance,
                startTime = Instant.ofEpochSecond(inputData.getLong(StepSensorViewModel.Key.START.value, 0L)),
                endTime = Instant.ofEpochSecond(inputData.getLong(StepSensorViewModel.Key.END.value, 0L))
            )
            setStepUseCase.setLastStep(inputData.getLong(StepSensorViewModel.Key.STEP_LAST_TIME.value, 0L))

            when (val yesterday = inputData.getLong(StepSensorViewModel.Key.YESTERDAY.value, 0L)) {
                0L -> {}
                else -> {
                    setStepUseCase.setYesterdayStep(yesterday)
                }
            }
            //Log.d("test","dowork d: ${inputData.getLong(StepSensorModule.Key.DISTANCE.value, 0L)}")
        }

        return Result.success()
    }
}