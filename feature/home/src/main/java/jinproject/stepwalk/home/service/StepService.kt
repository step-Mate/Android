package jinproject.stepwalk.home.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
@AndroidEntryPoint
internal class StepService : LifecycleService() {

    @Inject
    lateinit var stepSensorViewModel: StepSensorViewModel

    private var stepNotiLayout: RemoteViews? = null
    private var notification: NotificationCompat.Builder? = null
    private var exitFlag: Boolean? = null

    override fun onCreate() {
        super.onCreate()

        lifecycle.addObserver(stepSensorViewModel.viewModelScope as LifecycleEventObserver)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createChannel()

        setNotification()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                stepSensorViewModel.steps.collectLatest { stepData ->
                    stepNotiLayout?.setTextViewText(R.id.tv_stepHeader, stepData.current.toString())
                    notificationManager.notify(NOTIFICATION_ID, notification?.build())
                }
            }
        }
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
                stepSensorViewModel.setStepInsertWorker(
                    Data
                        .Builder()
                        .putLong(StepSensorViewModel.Key.YESTERDAY.value, stepSensorViewModel.steps.value.current + stepSensorViewModel.steps.value.yesterday)
                        .putLong(StepSensorViewModel.Key.STEP_LAST_TIME.value, 0L)
                        .putLong(StepSensorViewModel.Key.DISTANCE.value, stepSensorViewModel.steps.value.current - stepSensorViewModel.steps.value.last)
                        .putLong(StepSensorViewModel.Key.START.value, stepSensorViewModel.startTime.onKorea().toEpochSecond())
                        .putLong(StepSensorViewModel.Key.END.value, stepSensorViewModel.endTime.onKorea().toEpochSecond())
                        .build()
                )
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        if (::stepSensorViewModel.isInitialized)
            stepSensorViewModel.unRegisterSensor()

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
            setTextViewText(R.id.tv_stepHeader, stepSensorViewModel.steps.value.current.toString())
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