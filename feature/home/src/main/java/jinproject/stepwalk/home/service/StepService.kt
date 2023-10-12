package jinproject.stepwalk.home.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import jinproject.stepwalk.domain.model.METs
import jinproject.stepwalk.domain.usecase.GetStepUseCase
import jinproject.stepwalk.domain.usecase.SetStepUseCase
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.R
import jinproject.stepwalk.home.utils.StepWalkChannelId
import jinproject.stepwalk.home.utils.createChannel
import jinproject.stepwalk.home.utils.onKorea
import jinproject.stepwalk.home.worker.RestartServiceWorker
import jinproject.stepwalk.home.worker.StepInsertWorker
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
internal class StepService : LifecycleService() {

    @Inject
    lateinit var healthConnector: HealthConnector

    @Inject
    lateinit var setStepUseCase: SetStepUseCase

    @Inject
    lateinit var getStepUseCase: GetStepUseCase

    private lateinit var sensorModule: StepSensorModule
    private var stepNotiLayout: RemoteViews? = null
    private var notification: NotificationCompat.Builder? = null
    private var exitFlag: Boolean? = null
    private val steps by lazy { Array(3) { 0L } }

    override fun onCreate() {
        super.onCreate()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createChannel()

        setNotification()

        lifecycleScope.launch {
            val step = healthConnector.getTodayTotalStep(METs.Walk)
            setStepUseCase.setTodayStep(step)

            getStepUseCase().onEach { steps ->
                repeat(3) { idx ->
                    this@StepService.steps[idx] = steps[idx]
                }

                stepNotiLayout?.setTextViewText(R.id.tv_stepHeader, steps[0].toString())
                notificationManager.notify(NOTIFICATION_ID, notification?.build())
            }.collect()
        }

        sensorModule = StepSensorModule(
            context = this@StepService,
            steps = steps,
            onSensorChanged = { today, last ->
                lifecycleScope.launch {
                    setStepUseCase.setTodayStep(today)
                    setStepUseCase.setYesterdayStep(last)
                }
            }
        )

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        exitFlag = intent?.getBooleanExtra("exit", false) ?: false
        val alarmFlag = intent?.getBooleanExtra("alarm", false) ?: false

        when {
            exitFlag == true -> {
                stopSelf()
            }
            alarmFlag -> {
                val workRequest = OneTimeWorkRequestBuilder<StepInsertWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setInputData(
                        Data
                            .Builder()
                            .putLong(StepSensorModule.Key.YESTERDAY.value, steps[0] + steps[1])
                            .putLong(StepSensorModule.Key.STEP_LAST_TIME.value, 0L)
                            .putLong(StepSensorModule.Key.DISTANCE.value, steps[0] - steps[2])
                            .putLong(StepSensorModule.Key.START.value, sensorModule.startTime.onKorea().toEpochSecond())
                            .putLong(StepSensorModule.Key.END.value, sensorModule.endTime.onKorea().toEpochSecond())
                            .build()
                    )
                    .build()

                WorkManager
                    .getInstance(this)
                    .enqueueUniqueWork(
                        "insertStepWork",
                        ExistingWorkPolicy.REPLACE,
                        workRequest
                    )

                sensorModule.startTime = LocalDateTime.now()
                sensorModule.endTime = LocalDateTime.now()

            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        if (::sensorModule.isInitialized)
            sensorModule.unRegisterSensor()

        if (exitFlag == false) {
            val workRequest = OneTimeWorkRequestBuilder<RestartServiceWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()

            WorkManager
                .getInstance(this)
                .enqueueUniqueWork(
                    "restartStepService",
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }
        super.onDestroy()
    }

    private fun setNotification() {

        val exitIntent = Intent(this, StepService::class.java).apply {
            putExtra("exit", true)
        }

        val exitPendingIntent = PendingIntent.getService(
            this,
            NOTIFICATION_ID,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        stepNotiLayout = RemoteViews(packageName, R.layout.step_notification).apply {
            setTextViewText(R.id.tv_stepHeader, steps.first().toString())
        }

        notification =
            NotificationCompat.Builder(this, StepWalkChannelId)
                .setSmallIcon(jinproject.stepwalk.design.R.drawable.ic_person_walking)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(stepNotiLayout)
                .setCustomBigContentView(stepNotiLayout)
                .setOngoing(true)
                .addAction(jinproject.stepwalk.design.R.drawable.ic_time, "끄기", exitPendingIntent)

        startForeground(NOTIFICATION_ID, notification?.build())
    }

    companion object {
        const val NOTIFICATION_ID = 999
    }
}