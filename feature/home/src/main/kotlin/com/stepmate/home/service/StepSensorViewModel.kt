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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val updateMissionUseCases: UpdateMissionUseCases,
    private val checkUpdateMissionUseCases: CheckUpdateMissionUseCases,
    private val resetMissionTimeUseCases: ResetMissionTimeUseCases,
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

    private val _designation: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val designation: StateFlow<List<String>> get() = _designation.asStateFlow()

    val viewModelScope: CoroutineScope =
        ViewModelCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate + coroutineExceptionHandler)

    private val sensorTimeScheduler = TimeScheduler(
        scope = viewModelScope,
        callBack = {
            withContext(Dispatchers.IO) {
                updateStepBySensor()
                checkUpdateMissionList()
            }
        }
    )

    private var isRecreated = true

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
                last = todayStep
            )
        }
    }

    suspend fun onSensorChanged(stepBySensor: Long) {
        _step.update { state -> state.getTodayStep(stepBySensor, isRecreated) }

        if (isRecreated)
            isRecreated = false

        manageStepUseCase.setTodayStep(step.value.current)

        if (!sensorTimeScheduler.isRunning)
            startTime = ZonedDateTime.now()

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

            _step.update { state -> state.copy(last = step.value.current) }

            if (isLoginUser) {
                setUserDayStepUseCase.addStep(walked.toInt())
                updateMissionUseCases(walked.toInt())
            }
        }
    }

    fun getStepInsertWorkerUpdatingOnNewDay() {
        viewModelScope.launch(Dispatchers.IO) {
            val walked = step.value.current - step.value.last

            healthConnector.insertSteps(
                step = walked,
                startTime = startTime,
                endTime = endTime,
            )

            _step.update { state ->
                state.copy(
                    last = 0L,
                    yesterday = step.value.current + step.value.yesterday - step.value.stepAfterReboot,
                    stepAfterReboot = 0L,
                    current = 0L
                )
            }

            manageStepUseCase.setTodayStep(0L)

            startTime = ZonedDateTime.now()
            endTime = ZonedDateTime.now()

            if (isLoginUser) {
                setUserDayStepUseCase.queryDailyStep(
                    step.value.current.toInt()
                )
                updateMissionUseCases(walked.toInt())
            }
        }
    }

    fun resetTimeMission() {
        viewModelScope.launch(Dispatchers.IO) {
            resetMissionTimeUseCases()
        }
    }

    private suspend fun checkUpdateMissionList() {
        if (isLoginUser) {
            val completeList = checkUpdateMissionUseCases()
            if (completeList.isNotEmpty()) {
                _designation.update { completeList }
            }
        }
    }
}

object StepException {
    const val NEED_RE_LOGIN = "NEED RE LOGIN"
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