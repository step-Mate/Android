package jinproject.stepwalk.home.service

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import jinproject.stepwalk.design.component.lazyList.TimeScheduler
import jinproject.stepwalk.domain.model.StepData
import jinproject.stepwalk.domain.usecase.step.ManageStepUseCase
import jinproject.stepwalk.domain.usecase.step.SetUserDayStepUseCase
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.worker.MissionUpdateWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class StepSensorViewModel @Inject constructor(
    private val setUserDayStepUseCase: SetUserDayStepUseCase,
    private val manageStepUseCase: ManageStepUseCase,
    private val healthConnector: HealthConnector,
) {
    private var startTime: ZonedDateTime = ZonedDateTime.now()
    private var endTime: ZonedDateTime = ZonedDateTime.now()

    private val _step: MutableStateFlow<StepData> = MutableStateFlow(StepData.getInitValues())
    val step: StateFlow<StepData> get() = _step.asStateFlow()

    val viewModelScope: CoroutineScope =
        ViewModelCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val sensorTimeScheduler = TimeScheduler(
        scope = viewModelScope,
        callBack = {
            updateStepBySensor()
        }
    )

    private var isRecreated = true

    init {
        viewModelScope.launch {
            val todayStep = healthConnector.getTodayTotalStep()
            val diff = manageStepUseCase.getTodayStep().first() - todayStep

            val notAddedStep = if (diff > 0) diff else 0L

            _step.update { state ->
                state.copy(
                    current = todayStep,
                    stepAfterReboot = todayStep + notAddedStep,
                    last = todayStep
                )
            }
        }
    }

    suspend fun onSensorChanged(stepBySensor: Long) {
        _step.update { state -> state.getTodayStep(stepBySensor, isRecreated) }

        if (isRecreated)
            isRecreated = false

        manageStepUseCase.setTodayStep(step.value.current)

        sensorTimeScheduler.setTime(60 * 1000)

        endTime = ZonedDateTime.now()
    }

    private suspend fun updateStepBySensor() {
        val walked = step.value.current - step.value.last

        if (walked > 0) {
            healthConnector.insertSteps(
                step = walked,
                startTime = startTime,
                endTime = endTime,
            )
            setUserDayStepUseCase.addStep(
                walked.toInt()
            )
        }

        _step.update { state -> state.copy(last = step.value.current) }

        startTime = ZonedDateTime.now()
    }

    fun getStepInsertWorkerUpdatingOnNewDay() {
        viewModelScope.launch(Dispatchers.IO) {
            healthConnector.insertSteps(
                step = step.value.current - step.value.last,
                startTime = startTime,
                endTime = endTime,
            )
            setUserDayStepUseCase.queryDailyStep(
                step.value.current.toInt()
            )

            _step.update { state ->
                state.copy(
                    last = 0L,
                    yesterday = step.value.current + step.value.yesterday - step.value.stepAfterReboot,
                    stepAfterReboot = 0L,
                    current = 0L
                )
            }
            startTime = ZonedDateTime.now()
            endTime = ZonedDateTime.now()
        }
    }

    fun getMissionUpdateWorker(): OneTimeWorkRequest {
        val inputData = Data.Builder()
            .putLong(KEY_DISTANCE, step.value.current - step.value.last)
            .build()
        return OneTimeWorkRequestBuilder<MissionUpdateWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(inputData)
            .build()
    }

    fun getStepInsertWorkerUpdatingOnNewDay() {
        viewModelScope.launch(Dispatchers.IO) {
            healthConnector.insertSteps(
                step = step.value.current - step.value.last,
                startTime = startTime,
                endTime = endTime,
            )
            setUserDayStepUseCase.queryDailyStep(
                step.value.current.toInt()
            )

            _step.update { state ->
                state.copy(
                    last = 0L,
                    yesterday = step.value.current + step.value.yesterday - step.value.stepAfterReboot,
                    stepAfterReboot = 0L,
                    current = 0L
                )
            }
            startTime = ZonedDateTime.now()
            endTime = ZonedDateTime.now()
        }
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