package com.stepmate.home.service

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.widget.RemoteViews
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.stepmate.home.R
import com.stepmate.home.utils.StepMateChannelId
import com.stepmate.home.utils.createChannel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.ZonedDateTime
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
            context = this@StepService,
            onSensorChanged = { event ->
                val stepBySensor = event?.values?.first()?.toLong() ?: 0L

                lifecycleScope.launch {
                    stepSensorViewModel.onSensorChanged(stepBySensor)
                }
            }
        )
    }

    private val dailyStepPendingIntent by lazy {
        PendingIntent.getForegroundService(
            this,
            700,
            Intent(this, StepService::class.java).apply {
                putExtra("alarm", true)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private var isCreated = true

    override fun onCreate() {
        super.onCreate()

        lifecycle.addObserver(stepSensorViewModel.viewModelScope as LifecycleEventObserver)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createChannel()

        setNotification()

        lifecycleScope.launch {
            launch {
                stepSensorViewModel.step.collectLatest { stepData ->
                    stepNotiLayout?.setTextViewText(R.id.tv_stepHeader, stepData.current.toString())
                    if (::notification.isInitialized)
                        notificationManager.notify(NOTIFICATION_STEP_ID, notification)
                }
            }

            launch {
                stepSensorViewModel.exception.collectLatest { e ->
                    when (e) {
                        StepException.NEED_RE_LOGIN -> {
                            startActivity(
                                Intent(
                                    this@StepService,
                                    Class.forName(StepMateActivity)
                                ).apply {
                                    putExtra(
                                        StepException.NEED_RE_LOGIN,
                                        StepException.NEED_RE_LOGIN
                                    )
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                })
                        }

                        else -> {
                        }
                    }
                }
            }

            launch {
                stepSensorViewModel.missionUpdate.collectLatest {
                    setWorker(it.second)
                }
            }
        }

        alarmResettingDailyStep()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        exitFlag = intent?.getBooleanExtra("exit", false) ?: false
        val alarmFlag = intent?.getBooleanExtra("alarm", false) ?: false

        when {
            exitFlag!! -> {
                stopSelf()
            }

            alarmFlag -> {
                val today = ZonedDateTime.now().dayOfWeek
                stepSensorViewModel.getStepInsertWorkerUpdatingOnNewDay()
                if (today == DayOfWeek.MONDAY)
                    stepSensorViewModel.resetTimeMission()
                alarmResettingDailyStep()
            }

            else -> {
                if (intent == null || isCreated) {
                    lifecycleScope.launch {
                        stepSensorViewModel.initStep()
                        registerSensor()

                        if (intent == null)
                            stepSensorViewModel.setYesterdayStepIfKilledBySystem()
                        else if (isCreated) {
                            stepSensorViewModel.initYesterdayStep()
                            isCreated = false
                        }
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                        startForeground(
                            NOTIFICATION_STEP_ID, notification,
                            ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
                        )
                    else
                        startForeground(NOTIFICATION_STEP_ID, notification)
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        unRegisterSensor()
        cancelResettingDailyStepAlarm()

        super.onDestroy()
    }

    private fun setNotification() {
        val exitPendingIntent = PendingIntent.getService(
            this,
            NOTIFICATION_STEP_ID,
            Intent(this, StepService::class.java).apply {
                putExtra("exit", true)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val contentPendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_STEP_ID,
            Intent(this, Class.forName(StepMateActivity)),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        stepNotiLayout = RemoteViews(packageName, R.layout.step_notification).apply {
            setTextViewText(R.id.tv_stepHeader, stepSensorViewModel.step.value.current.toString())
        }

        notification =
            NotificationCompat.Builder(this, StepMateChannelId)
                .setSmallIcon(com.stepmate.design.R.drawable.ic_stepmate_shoes)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(stepNotiLayout)
                .setCustomBigContentView(stepNotiLayout)
                .setColor(Color(0xFFA5D6A7).toArgb())
                .setOngoing(true)
                .addAction(com.stepmate.design.R.drawable.ic_time, "끄기", exitPendingIntent)
                .setContentIntent(contentPendingIntent)
                .build()
    }

    private suspend fun registerSensor() = withContext(stepSensorViewModel.sensorDispatcher) {
        stepSensorManager.registerSensor()
    }

    private fun unRegisterSensor() {
        stepSensorManager.unRegisterSensor()
    }

    private fun alarmResettingDailyStep() {
        val time = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val alarmClock = AlarmManager.AlarmClockInfo(time.timeInMillis, dailyStepPendingIntent)

        alarmManager.setAlarmClock(
            alarmClock,
            dailyStepPendingIntent
        )
    }

    private fun setWorker(walked: Long) {
        WorkManager
            .getInstance(this)
            .beginUniqueWork(
                "missionUpdateWorker",
                ExistingWorkPolicy.REPLACE,
                stepSensorViewModel.getMissionUpdateWorker(walked)
            )
            .then(stepSensorViewModel.getMissionCheckWorker()).enqueue()
    }

    private fun cancelResettingDailyStepAlarm() {
        alarmManager.cancel(dailyStepPendingIntent)
    }

    companion object {
        private const val NOTIFICATION_STEP_ID = 999
        private const val StepMateActivity = "com.stepmate.app.StepMateActivity"
    }
}