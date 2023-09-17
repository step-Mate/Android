package jinproject.stepwalk.home.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
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
import jinproject.stepwalk.home.worker.RestartServiceWorker
import kotlinx.coroutines.launch
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
    private var exitFlag: Boolean? = null

    override fun onCreate() {
        super.onCreate()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createChannel()

        lifecycleScope.launch {
            sensorModule = StepSensorModule(
                context = this@StepService,
                currentTotalStep = healthConnector.getTotalStepToday(METs.Walk),
                onSensorChanged = { stepCounter ->
                    stepNotiLayout?.setTextViewText(R.id.tv_stepHeader, stepCounter.toString())
                    notificationManager.notify(NOTIFICATION_ID, notification?.build())

                    lifecycleScope.launch {
                        setStepUseCase(stepCounter)
                    }
                }
            )
            setNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        exitFlag = intent?.getBooleanExtra("exit", false) ?: false

        when {
            exitFlag == true -> {
                stopSelf()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        if (::sensorModule.isInitialized)
            sensorModule.unRegisterSensor()

        if(exitFlag != true) {
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
            setTextViewText(R.id.tv_stepHeader, sensorModule.stepCounterTotal.toString())
        }

        notification =
            NotificationCompat.Builder(this, StepWalkChannelId)
                .setSmallIcon(jinproject.stepwalk.design.R.drawable.ic_person_walking)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(stepNotiLayout)
                .setCustomBigContentView(stepNotiLayout)
                .setOngoing(true)

        startForeground(NOTIFICATION_ID, notification?.build())
    }

    companion object {
        const val NOTIFICATION_ID = 999
    }
}