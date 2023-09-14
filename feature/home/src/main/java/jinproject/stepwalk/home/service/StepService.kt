package jinproject.stepwalk.home.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
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
import jinproject.stepwalk.domain.usecase.SetStepUseCase
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.R
import jinproject.stepwalk.home.utils.StepWalkChannelId
import jinproject.stepwalk.home.utils.createChannel
import jinproject.stepwalk.home.utils.onKorea
import jinproject.stepwalk.home.worker.StepInsertWorker
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
internal class StepService : LifecycleService() {

    @Inject
    lateinit var healthConnector: HealthConnector

    @Inject
    lateinit var setStepUseCase: SetStepUseCase

    private lateinit var sensorModule: StepSensorModule
    private var stepNotiLayout: RemoteViews? = null
    private var notification: NotificationCompat.Builder? = null
    private var startTime: LocalDateTime = LocalDateTime.now()
    private var endTime: LocalDateTime = LocalDateTime.now()

    override fun onCreate() {
        super.onCreate()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createChannel()

        lifecycleScope.launch {
            sensorModule = StepSensorModule(
                context = this@StepService,
                step = healthConnector.getTotalStepToday(METs.Walk),
                onSensorChanged = { stepCounter ->
                    stepNotiLayout?.setTextViewText(R.id.tv_stepHeader, stepCounter.toString())
                    startForeground(999, notification?.build())

                    lifecycleScope.launch {
                        setStepUseCase(stepCounter)
                    }

                    when {
                        (LocalDateTime.now().onKorea().toEpochSecond() - endTime.onKorea().toEpochSecond()) < 300 -> {
                            endTime = LocalDateTime.now()
                        }

                        else -> {
                            val workRequest = OneTimeWorkRequestBuilder<StepInsertWorker>()
                                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                                .setInputData(
                                    Data
                                        .Builder()
                                        .putLong("distance",sensorModule.getStepOnThisHour())
                                        .putLong("start", startTime.onKorea().toEpochSecond())
                                        .putLong("end", endTime.onKorea().toEpochSecond())
                                        .build()
                                )
                                .build()

                            WorkManager
                                .getInstance(this@StepService)
                                .enqueueUniqueWork(
                                    "insertStepWork",
                                    ExistingWorkPolicy.REPLACE,
                                    workRequest
                                )

                            startTime = LocalDateTime.now()
                            endTime = LocalDateTime.now()
                        }
                    }

                }
            )
            setNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val exit = intent?.getBooleanExtra("exit", false) ?: false

        when {
            exit -> {
                stopSelf()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        if (::sensorModule.isInitialized)
            sensorModule.unRegisterSensor()
        super.onDestroy()
    }

    private fun setNotification() {

        val exitIntent = Intent(this, StepService::class.java).apply {
            putExtra("exit", true)
        }

        val exitPendingIntent = PendingIntent.getService(
            this,
            999,
            exitIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        stepNotiLayout = RemoteViews(packageName, R.layout.step_notification).apply {
            setTextViewText(R.id.tv_stepHeader, sensorModule.stepCounterTotal.toString())
        }

        notification =
            NotificationCompat.Builder(this, StepWalkChannelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSmallIcon(jinproject.stepwalk.design.R.drawable.ic_person_walking)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(stepNotiLayout)
                .setCustomBigContentView(stepNotiLayout)
                .setOngoing(true)

        startForeground(999, notification?.build())
    }
}