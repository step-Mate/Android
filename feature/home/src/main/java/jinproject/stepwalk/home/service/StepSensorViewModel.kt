package jinproject.stepwalk.home.service

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import jinproject.stepwalk.domain.model.METs
import jinproject.stepwalk.domain.usecase.GetStepUseCase
import jinproject.stepwalk.domain.usecase.SetStepUseCase
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.receiver.AlarmReceiver
import jinproject.stepwalk.home.utils.setRepeating
import jinproject.stepwalk.home.utils.onKorea
import jinproject.stepwalk.home.worker.StepInsertWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Calendar
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

internal class StepSensorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getStepUseCase: GetStepUseCase,
    private val setStepUseCase: SetStepUseCase,
    private val healthConnector: HealthConnector
) {
    private val alarmManager: AlarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    var startTime: LocalDateTime = LocalDateTime.now()
    var endTime: LocalDateTime = LocalDateTime.now()

    private val _steps: MutableStateFlow<StepData> = MutableStateFlow(StepData.getInitValues())
    val steps: StateFlow<StepData> get() = _steps.asStateFlow()

    val viewModelScope: CoroutineScope =
        ViewModelCoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val stepSensorManager: StepSensorManager = StepSensorManager(
        context = context,
        onSensorChanged = { event ->
            val stepBySensor = event?.values?.first()?.toLong() ?: 0L

            val todayStep = steps.value.getTodayStep(
                stepBySensor = stepBySensor,
                isRebootDevice = isRebootDevice(stepBySensor),
                setStepYesterday = { yesterday ->
                    setStepYesterday(yesterday)
                }
            )

            setStepInsertWorkerByTime(todayStep)
        }
    )

    private fun setStepYesterday(yesterday: Long) {
        viewModelScope.launch {
            setStepUseCase.setYesterdayStep(yesterday)
        }
    }

    /**
     * 휴대폰 재부팅이 되었는지 확인하는 함수
     * @param stepBySensor 센서로 부터 측정된 전체 걸음수
     * @return 재부팅이면 true
     */
    private fun isRebootDevice(stepBySensor: Long): Boolean {
        return stepBySensor == 0L
    }


    init {
        registerSensor()
        alarmUpdatingLastStep()
        initStepByHealthConnect()
        getSteps()
    }

    private fun initStepByHealthConnect() {
        viewModelScope.launch {
            val today = healthConnector.getTodayTotalStep(METs.Walk)
            setStepUseCase.setTodayStep(today)
        }
    }

    private fun getSteps() {
        viewModelScope.launch {
            getStepUseCase().collectLatest { stepData ->
                _steps.update {
                    StepData(
                        current = stepData.current,
                        last = stepData.last,
                        yesterday = stepData.yesterday
                    )
                }
            }
        }
    }

    private fun registerSensor() {
        stepSensorManager.registerSensor()
    }

    fun unRegisterSensor() {
        stepSensorManager.unRegisterSensor()
    }

    private fun alarmUpdatingLastStep() {
        val time = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setRepeating(
            context = context,
            notifyIntent = {
                Intent(context, AlarmReceiver::class.java)
            },
            type = AlarmManager.RTC_WAKEUP,
            time = time.timeInMillis,
            interval = AlarmManager.INTERVAL_DAY
        )

    }

    private fun setStepInsertWorkerByTime(todayStep: Long) {
        when {
            (LocalDateTime.now().onKorea().toEpochSecond() - endTime.onKorea().toEpochSecond()) < 60 -> {
                endTime = LocalDateTime.now()
            }

            else -> {
                setStepInsertWorker(
                    Data
                        .Builder()
                        .putLong(Key.DISTANCE.value, todayStep - steps.value.last)
                        .putLong(Key.START.value, startTime.onKorea().toEpochSecond())
                        .putLong(Key.END.value, endTime.onKorea().toEpochSecond())
                        .putLong(Key.STEP_LAST_TIME.value, todayStep)
                        .build()
                )
            }
        }
    }

    fun setStepInsertWorker(data: Data) {
        val workRequest = OneTimeWorkRequestBuilder<StepInsertWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setInputData(
                data
            )
            .build()

        WorkManager
            .getInstance(context)
            .enqueueUniqueWork(
                "insertStepWork",
                ExistingWorkPolicy.REPLACE,
                workRequest
            )

        startTime = LocalDateTime.now()
        endTime = LocalDateTime.now()
    }

    data class StepData(
        val current: Long,
        val last: Long,
        val yesterday: Long
    ) {

        /**
         * 오늘의 걸음수를 계산하여 가져오는 함수
         * @param stepBySensor 센서로 부터 측정된 전체 걸음수
         * @return 오늘의 걸음수
         */
        fun getTodayStep(
            stepBySensor: Long,
            isRebootDevice: Boolean,
            setStepYesterday: (Long) -> Unit
        ): Long =
            when (isRebootDevice) {
                true -> {
                    setStepYesterday(0L)
                    current
                }

                false -> {
                    if (isNewInstall()) {
                        setStepYesterday(stepBySensor)
                    }
                    stepBySensor - yesterday
                }
            }

        private fun isNewInstall(): Boolean {
            return current == 0L && yesterday == 0L
        }

        companion object {
            fun getInitValues() = StepData(
                current = 0L,
                last = 0L,
                yesterday = 0L
            )
        }
    }

    enum class Key(val value: String) {
        DISTANCE("distance"),
        START("start"),
        END("end"),
        STEP_LAST_TIME("stepLastTime"),
        YESTERDAY("yesterday")
    }
}

internal class StepSensorManager(
    context: Context,
    onSensorChanged: (SensorEvent?) -> Unit
) {
    private val sensorManager: SensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val stepSensor: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) }

    private val stepListener: SensorEventListener =
        object : SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {
                onSensorChanged(p0)
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

        }

    fun registerSensor() {
        sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun unRegisterSensor() {
        sensorManager.unregisterListener(stepListener, stepSensor)
    }
}

internal class ViewModelCoroutineScope(
    context: CoroutineContext
) : LifecycleEventObserver, CoroutineScope {
    override val coroutineContext: CoroutineContext = context

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            coroutineContext.cancel()
            source.lifecycle.removeObserver(this)
        }
    }
}