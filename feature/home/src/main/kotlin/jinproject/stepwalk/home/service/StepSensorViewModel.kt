package jinproject.stepwalk.home.service

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import jinproject.stepwalk.domain.model.StepData
import jinproject.stepwalk.domain.usecase.GetStepUseCase
import jinproject.stepwalk.domain.usecase.SetStepUseCase
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.worker.StepInsertWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class StepSensorViewModel @Inject constructor(
    private val getStepUseCase: GetStepUseCase,
    private val setStepUseCaseImpl: SetStepUseCase,
    private val healthConnector: HealthConnector,
) {
    private var startTime: ZonedDateTime = ZonedDateTime.now()
    private var endTime: ZonedDateTime = ZonedDateTime.now()

    private val _steps: MutableStateFlow<StepData> = MutableStateFlow(StepData.getInitValues())
    val steps: StateFlow<StepData> get() = _steps.asStateFlow()

    val viewModelScope: CoroutineScope =
        ViewModelCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    init {
        getSteps()
    }

    private fun getSteps() {
        viewModelScope.launch {
            getStepUseCase().collectLatest { stepData ->
                _steps.update {
                    stepData
                }
            }
        }
    }

    suspend fun onSensorChanged(stepBySensor: Long): OneTimeWorkRequest? {
        var todayStep = steps.value.getTodayStep(
            stepBySensor = stepBySensor,
        )

        if (steps.value.isNewInstall()) {
            val step = healthConnector.getTodayTotalStep()
            todayStep = todayStep.copy(
                yesterday = todayStep.yesterday - step,
                current = step
            )
        }

        return getStepInsertWorkerByTime(todayStep)
    }

    private fun setStepData(stepData: StepData) {
        viewModelScope.launch {
            setStepUseCaseImpl(stepData)
        }
    }

    private fun getStepInsertWorkerByTime(todayStep: StepData): OneTimeWorkRequest? {
        if (ZonedDateTime.now().toEpochSecond() - endTime.toEpochSecond() < 60) {
            endTime = ZonedDateTime.now()
            setStepData(todayStep)
            return null
        } else
            return getStepInsertOneTimeWorker(
                Data
                    .Builder()
                    .putLong(KEY_DISTANCE, todayStep.current - todayStep.last)
                    .putLong(KEY_STEP_LAST_TIME, todayStep.current)
            )
    }

    fun getStepInsertWorkerUpdatingOnNewDay(): OneTimeWorkRequest {
        val data = Data.Builder()
            .putLong(KEY_YESTERDAY, steps.value.current + steps.value.yesterday)
            .putLong(KEY_STEP_LAST_TIME, 0L)
            .putLong(KEY_DISTANCE, steps.value.current - steps.value.last)
            .putBoolean(KEY_IS_REBOOT, false)

        return getStepInsertOneTimeWorker(data)
    }

    private fun getStepInsertOneTimeWorker(data: Data.Builder): OneTimeWorkRequest {
        val inputData = data
            .putLong(KEY_START, startTime.toEpochSecond())
            .putLong(KEY_END, endTime.toEpochSecond())
            .build()

        startTime = ZonedDateTime.now()
        endTime = ZonedDateTime.now()

        return OneTimeWorkRequestBuilder<StepInsertWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(inputData)
            .build()
    }

    companion object {
        const val KEY_DISTANCE = "distance"
        const val KEY_START = "start"
        const val KEY_END = "end"
        const val KEY_STEP_LAST_TIME = "stepLastTime"
        const val KEY_YESTERDAY = "yesterday"
        const val KEY_IS_REBOOT = "isReboot"
    }
}

internal class ViewModelCoroutineScope(
    context: CoroutineContext,
) : LifecycleEventObserver, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            coroutineContext.cancel()
            source.lifecycle.removeObserver(this)
        }
    }
}