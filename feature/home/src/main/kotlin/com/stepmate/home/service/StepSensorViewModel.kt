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
import com.stepmate.domain.usecase.mission.UpdateMissionUseCases
import com.stepmate.domain.usecase.step.ManageStepUseCase
import com.stepmate.domain.usecase.step.SetUserDayStepUseCase
import com.stepmate.home.HealthConnector
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
    checkHasTokenUseCase: CheckHasTokenUseCase,
) {
    private var startTime: ZonedDateTime = ZonedDateTime.now()
    private var endTime: ZonedDateTime = ZonedDateTime.now()

    private val _step: MutableStateFlow<StepData> = MutableStateFlow(StepData.getInitValues())
    val step: StateFlow<StepData> get() = _step.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, t ->
        when (t) {
            is StepMateHttpException -> {
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
            updateStepBySensor()
            if (isLoginUser)
                checkUpdateMissionList()
        }
    )

    private var isRecreated = true
    private var isLoginUser = false

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

        checkHasTokenUseCase().onEach { bool ->
            isLoginUser = bool
        }.launchIn(viewModelScope)
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

            if (isLoginUser) {
                setUserDayStepUseCase.addStep(
                    walked.toInt()
                )
                updateMissionUseCases(walked.toInt())
            }
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

            if (isLoginUser)
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

    private suspend fun checkUpdateMissionList() = withContext(Dispatchers.IO) {
        val completeList = checkUpdateMissionUseCases()
        if (completeList.isNotEmpty()) {
            _designation.update { completeList }
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