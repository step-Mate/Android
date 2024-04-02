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
import com.stepmate.home.R
import com.stepmate.home.utils.StepMateChannelId
import com.stepmate.home.utils.createChannel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @OptIn(ExperimentalCoroutinesApi::class)
    val serviceDispatcher = Dispatchers.IO.limitedParallelism(1)

    private val alarmManager: AlarmManager by lazy { getSystemService(Context.ALARM_SERVICE) as AlarmManager }
    private lateinit var stepSensorManager: StepSensorManager

    private val missionPendingIntent by lazy {
        PendingIntent.getForegroundService(
            this,
            800,
            Intent(this, StepService::class.java).apply {
                putExtra("missionClear", true)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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

    override fun onCreate() {
        super.onCreate()
        lifecycle.addObserver(stepSensorViewModel.viewModelScope as LifecycleEventObserver)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createChannel()

        setNotification()

        lifecycleScope.launch {
            stepSensorViewModel.initStep()

            stepSensorManager = StepSensorManager(
                context = this@StepService,
                onSensorChanged = { event ->
                    launch(serviceDispatcher) {
                        val stepBySensor = event?.values?.first()?.toLong() ?: 0L
                        stepSensorViewModel.onSensorChanged(stepBySensor)
                    }
                }
            )

            registerSensor()

            launch {
                stepSensorViewModel.step.collectLatest { stepData ->
                    stepNotiLayout?.setTextViewText(R.id.tv_stepHeader, stepData.current.toString())
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
                stepSensorViewModel.designation.collectLatest { completeList ->
                    completeList.forEach { designation ->
                        notificationManager.notify(
                            NOTIFICATION_MISSION_ID,
                            setMissionNotification(designation)
                        )
                    }
                }
            }
        }

        alarmResettingDailyStep()
        alarmResettingWeeklyMission()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        exitFlag = intent?.getBooleanExtra("exit", false) ?: false
        val alarmFlag = intent?.getBooleanExtra("alarm", false) ?: false
        val missionFlag = intent?.getBooleanExtra("missionClear", false) ?: false

        when {
            exitFlag == true -> {
                cancelResettingWeeklyMissionAlarm()
                stopSelf()
            }

            alarmFlag -> {
                stepSensorViewModel.getStepInsertWorkerUpdatingOnNewDay()
                alarmResettingDailyStep()
            }

            missionFlag -> {
                stepSensorViewModel.resetTimeMission()
                alarmResettingWeeklyMission()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        unRegisterSensor()
        cancelResettingWeeklyMissionAlarm()
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            startForeground(
                NOTIFICATION_STEP_ID, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        else
            startForeground(NOTIFICATION_STEP_ID, notification)
    }

    private fun registerSensor() {
        if (::stepSensorManager.isInitialized)
            stepSensorManager.registerSensor()
    }

    private fun unRegisterSensor() {
        if (::stepSensorManager.isInitialized)
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

    private fun setMissionNotification(designation: String): Notification =
        NotificationCompat.Builder(this, StepMateChannelId)
            .setSmallIcon(com.stepmate.design.R.drawable.ic_person_walking)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentTitle("미션 달성")
            .setContentText("$designation 을 완료하였습니다.")
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    NOTIFICATION_MISSION_ID,
                    Intent(this, Class.forName(StepMateActivity)),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setOngoing(true)
            .build()

    private fun alarmResettingWeeklyMission() {
        val time = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            add(Calendar.DATE, 7)
        }
        val alarmClock =
            AlarmManager.AlarmClockInfo(time.timeInMillis, missionPendingIntent)

        alarmManager.setAlarmClock(
            alarmClock,
            missionPendingIntent
        )
    }

    private fun cancelResettingWeeklyMissionAlarm() {
        alarmManager.cancel(missionPendingIntent)
    }

    private fun cancelResettingDailyStepAlarm() {
        alarmManager.cancel(dailyStepPendingIntent)
    }

    companion object {
        private const val NOTIFICATION_STEP_ID = 999
        private const val NOTIFICATION_MISSION_ID = 100
        private const val StepMateActivity = "com.stepmate.app.StepMateActivity"
    }
}