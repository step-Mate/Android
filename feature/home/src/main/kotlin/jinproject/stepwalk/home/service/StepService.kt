package jinproject.stepwalk.home.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import jinproject.stepwalk.home.R
import jinproject.stepwalk.home.receiver.AlarmReceiver
import jinproject.stepwalk.home.utils.StepWalkChannelId
import jinproject.stepwalk.home.utils.createChannel
import jinproject.stepwalk.home.utils.setRepeating
import jinproject.stepwalk.home.worker.RestartServiceWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
internal class StepService : LifecycleService() {

    @Inject
    lateinit var stepSensorViewModel: StepSensorViewModel

    private var stepNotiLayout: RemoteViews? = null
    private lateinit var notification: Notification
    private var exitFlag: Boolean? = null

    private val alarmManager: AlarmManager by lazy { getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    private val stepSensorManager: StepSensorManager by lazy {
        StepSensorManager(
            context = this,
            onSensorChanged = { event ->
                val stepBySensor = event?.values?.first()?.toLong() ?: 0L
                lifecycleScope.launch(Dispatchers.IO) {
                    stepSensorViewModel.onSensorChanged(stepBySensor)?.let { worker ->
                        setWorker(worker)
                    }
                }
            }
        )
    }

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
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }
            }
        }

        registerSensor()
        alarmUpdatingLastStep()
    }

    private fun registerSensor() {
        stepSensorManager.registerSensor()
    }

    private fun unRegisterSensor() {
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
            context = this,
            notifyIntent = {
                Intent(this, AlarmReceiver::class.java)
            },
            type = AlarmManager.RTC_WAKEUP,
            time = time.timeInMillis,
            interval = AlarmManager.INTERVAL_DAY
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
                setWorker(
                    stepSensorViewModel.getStepInsertWorkerUpdatingOnNewDay()
                )
            }
        }

        return START_STICKY
    }

    private fun setWorker(worker: OneTimeWorkRequest) {
        WorkManager
            .getInstance(this)
            .enqueueUniqueWork(
                "insertStepWork",
                ExistingWorkPolicy.REPLACE,
                worker
            )
    }

    override fun onDestroy() {
        if (::stepSensorViewModel.isInitialized)
            unRegisterSensor()

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
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            startForeground(NOTIFICATION_ID, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        startForeground(NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_ID = 999
    }
}