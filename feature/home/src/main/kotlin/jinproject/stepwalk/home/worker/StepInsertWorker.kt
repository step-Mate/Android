package jinproject.stepwalk.home.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.hasKeyWithValueOfType
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import jinproject.stepwalk.domain.model.StepData
import jinproject.stepwalk.domain.usecase.SetStepUseCaseImpl
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.service.StepSensorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@HiltWorker
class StepInsertWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val healthConnector: HealthConnector,
    private val setStepUseCaseImpl: SetStepUseCaseImpl,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val distance = inputData.getLong(StepSensorViewModel.KEY_DISTANCE, 0L)

        if (distance == 0L)
            return Result.failure(Data.Builder().putString("fail", "걸음수는 0이 될 수 없습니다.").build())

        withContext(Dispatchers.IO) {
            healthConnector.insertSteps(
                step = distance,
                startTime = ZonedDateTime.ofInstant(
                    Instant.ofEpochSecond(
                        inputData.getLong(
                            StepSensorViewModel.KEY_START,
                            0L
                        )
                    ), ZoneId.of("Asia/Seoul")
                ),
                endTime = ZonedDateTime.ofInstant(
                    Instant.ofEpochSecond(
                        inputData.getLong(
                            StepSensorViewModel.KEY_END,
                            0L
                        )
                    ), ZoneId.of("Asia/Seoul")
                )
            )
            setStepUseCaseImpl.setLastStep(
                inputData.getLong(
                    StepSensorViewModel.KEY_STEP_LAST_TIME,
                    0L
                )
            )

            if (inputData.hasKeyWithValueOfType<Long>(StepSensorViewModel.KEY_YESTERDAY))
                setStepUseCaseImpl.setYesterdayStep(
                    inputData.getLong(
                        StepSensorViewModel.KEY_YESTERDAY,
                        0L
                    )
                )
            if (inputData.hasKeyWithValueOfType<Boolean>(StepSensorViewModel.KEY_IS_REBOOT))
                setStepUseCaseImpl(
                    StepData.getInitValues().copy(
                        isReboot = inputData.getBoolean(
                            StepSensorViewModel.KEY_IS_REBOOT,
                            false
                        )
                    )
                )

        }

        return Result.success()
    }
}