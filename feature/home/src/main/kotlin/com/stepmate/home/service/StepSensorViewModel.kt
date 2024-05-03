package com.stepmate.home.service

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.stepmate.design.component.lazyList.TimeScheduler
import com.stepmate.domain.model.StepData
import com.stepmate.domain.model.exception.StepMateHttpException
import com.stepmate.domain.usecase.auth.CheckHasTokenUseCase
import com.stepmate.domain.usecase.mission.CheckUpdateMissionUseCases
import com.stepmate.domain.usecase.mission.ResetMissionTimeUseCases
import com.stepmate.domain.usecase.mission.UpdateMissionUseCases
import com.stepmate.domain.usecase.step.ManageStepUseCase
import com.stepmate.domain.usecase.step.SetUserDayStepUseCase
import com.stepmate.home.HealthConnector
import com.stepmate.home.service.StepException.NEED_RE_LOGIN
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class StepSensorViewModel @Inject constructor(
    private val setUserDayStepUseCase: SetUserDayStepUseCase,
    private val manageStepUseCase: ManageStepUseCase,
    private val healthConnector: HealthConnector,
    private val resetMissionTimeUseCases: ResetMissionTimeUseCases,
    private val updateMissionUseCases: UpdateMissionUseCases,
    private val checkUpdateMissionUseCases: CheckUpdateMissionUseCases,
    checkHasTokenUseCase: CheckHasTokenUseCase,
) {
    private var startTime: ZonedDateTime = ZonedDateTime.now()
    private var endTime: ZonedDateTime = ZonedDateTime.now()

    private val _step: MutableStateFlow<StepData> = MutableStateFlow(StepData.getInitValues())
    val step: StateFlow<StepData> get() = _step.asStateFlow()

    private val _exception = MutableStateFlow("")
    val exception get() = _exception.asStateFlow()

    private var isLoginUser = false

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, t ->
        when (t) {
            is StepMateHttpException -> {
                when (t.code) {
                    402 -> {
                        isLoginUser = false
                        _exception.update { NEED_RE_LOGIN }
                    }
                }

                Log.e("test", "HttpException [${t.code}] has occurred cuz of [${t.message}]")
            }

            else -> {
                Log.d("test", "error has occurred : ${t.message}")
            }
        }
    }

    private val _completeMissionList: MutableSharedFlow<List<String>> = MutableSharedFlow(
        replay = 0, extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val completeMissionList: SharedFlow<List<String>> = _completeMissionList.asSharedFlow()

    val viewModelScope: CoroutineScope =
        ViewModelCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + coroutineExceptionHandler)

    @OptIn(ExperimentalCoroutinesApi::class)
    val sensorDispatcher = Dispatchers.IO.limitedParallelism(1)

    private val sensorTimeScheduler = TimeScheduler(
        scope = viewModelScope,
        callBack = {
            withContext(Dispatchers.IO) {
                updateStepBySensor()
            }
        }
    )

    init {
        checkHasTokenUseCase().onEach { bool ->
            isLoginUser = bool
        }.launchIn(viewModelScope)
    }

    suspend fun initStep() {
        val todayStep = healthConnector.getTodayTotalStep()
        val diff = manageStepUseCase.getTodayStep().first() - todayStep

        val notAddedStep = if (diff > 0) diff else 0L

        _step.update { state ->
            state.copy(
                current = todayStep,
                stepAfterReboot = todayStep + notAddedStep,
                last = todayStep,
            )
        }
    }

    suspend fun getYesterdayStepIfKilledBySystem() {
        val yesterdayStep = manageStepUseCase.getYesterdayStep().first()

        _step.update { state ->
            state.copy(
                yesterday = yesterdayStep,
                stepAfterReboot = state.stepAfterReboot - state.current
            )
        }
    }

    suspend fun initYesterdayStep() {
        manageStepUseCase.setYesterdayStep(step.value.yesterday)
    }

    suspend fun onSensorChanged(stepBySensor: Long, second: Int = 30, isCreated: Boolean) =
        withContext(sensorDispatcher + coroutineExceptionHandler) {
            _step.update { state -> state.getTodayStep(stepBySensor, isCreated) }

            if (!isCreated) {
                manageStepUseCase.setTodayStep(step.value.current)

                if (!sensorTimeScheduler.isRunning)
                    startTime = ZonedDateTime.now()

                sensorTimeScheduler.setTime(second * 1000L)

                endTime = ZonedDateTime.now()
            }
        }

    private suspend fun updateStepBySensor() {
        val walked = step.value.current - step.value.last

        if (walked > 0) {
            healthConnector.insertSteps(
                step = walked,
                startTime = startTime,
                endTime = endTime,
            )

            _step.update { state -> state.copy(last = step.value.current) }

            if (isLoginUser) {
                setUserDayStepUseCase.addStep(walked.toInt())
                withContext(Dispatchers.IO + coroutineExceptionHandler) {
                    updateMissionUseCases(walked.toInt())
                    _completeMissionList.emit(checkUpdateMissionUseCases())
                }
            }
        }
    }

    fun getStepInsertWorkerUpdatingOnNewDay() {
        viewModelScope.launch(Dispatchers.IO) {
            val walked = step.value.current - step.value.last
            val yesterday = step.value.current + step.value.yesterday - step.value.stepAfterReboot

            healthConnector.insertSteps(
                step = walked,
                startTime = startTime,
                endTime = endTime,
            )

            _step.update { state ->
                state.copy(
                    last = 0L,
                    yesterday = yesterday,
                    stepAfterReboot = 0L,
                    current = 0L
                )
            }

            manageStepUseCase.setTodayStep(0L)
            manageStepUseCase.setYesterdayStep(yesterday)

            startTime = ZonedDateTime.now()
            endTime = ZonedDateTime.now()

            if (isLoginUser) {
                setUserDayStepUseCase.queryDailyStep(
                    step.value.current.toInt()
                )
            }
        }
    }

    fun resetTimeMission() {
        viewModelScope.launch(Dispatchers.IO) {
            resetMissionTimeUseCases()
        }
    }
}

object StepException {
    const val NEED_RE_LOGIN = "NEED RE LOGIN"
}

private class ViewModelCoroutineScope(
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