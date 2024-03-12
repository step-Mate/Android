package jinproject.stepwalk.home.service

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
import dagger.hilt.android.AndroidEntryPoint
import jinproject.stepwalk.home.R
import jinproject.stepwalk.home.utils.StepMateChannelId
import jinproject.stepwalk.home.utils.createChannel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
    private val stepSensorManager: StepSensorManager by lazy {
        StepSensorManager(
            context = this,
            onSensorChanged = { event ->
                lifecycleScope.launch(serviceDispatcher) {
                    val stepBySensor = event?.values?.first()?.toLong() ?: 0L
                    stepSensorViewModel.onSensorChanged(stepBySensor)
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
            stepSensorViewModel.step.collectLatest { stepData ->
                stepNotiLayout?.setTextViewText(R.id.tv_stepHeader, stepData.current.toString())
                notificationManager.notify(NOTIFICATION_ID, notification)
            }

        }
        stepSensorViewModel.designation.onEach { completeList ->
            completeList.forEach { designation ->
                notificationManager.notify(
                    NOTIFICATION_MISSION_ID,
                    setMissionNotification(designation)
                )
            }
        }.launchIn(lifecycleScope)

        registerSensor()
        alarmUpdatingDayStep()
    }

    private fun registerSensor() {
        stepSensorManager.registerSensor()
    }

    private fun unRegisterSensor() {
        stepSensorManager.unRegisterSensor()
    }

    private fun alarmUpdatingDayStep() {
        val time = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val notifyPendingIntent = PendingIntent.getForegroundService(
            this,
            700,
            Intent(this, StepService::class.java).apply {
                putExtra("alarm", true)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            time.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            notifyPendingIntent
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
                stepSensorViewModel.getStepInsertWorkerUpdatingOnNewDay()
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        if (::stepSensorViewModel.isInitialized)
            unRegisterSensor()

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

        val contentPendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID,
            Intent(this, Class.forName("jinproject.stepwalk.app.StepMateActivity")),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        stepNotiLayout = RemoteViews(packageName, R.layout.step_notification).apply {
            setTextViewText(R.id.tv_stepHeader, stepSensorViewModel.step.value.current.toString())
        }

        notification =
            NotificationCompat.Builder(this, StepMateChannelId)
                .setSmallIcon(jinproject.stepwalk.design.R.drawable.ic_stepmate_shoes)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(stepNotiLayout)
                .setCustomBigContentView(stepNotiLayout)
                .setColor(Color(0xFFA5D6A7).toArgb())
                .setOngoing(true)
                .addAction(jinproject.stepwalk.design.R.drawable.ic_time, "끄기", exitPendingIntent)
                .setContentIntent(contentPendingIntent)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            startForeground(
                NOTIFICATION_ID, notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_HEALTH
            )
        else
            startForeground(NOTIFICATION_ID, notification)
    }

    private fun setMissionNotification(designation: String): Notification =
        NotificationCompat.Builder(applicationContext, StepMateChannelId)
            .setSmallIcon(jinproject.stepwalk.design.R.drawable.ic_person_walking)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentTitle("미션 달성")
            .setContentText("$designation 을 완료하였습니다.")
            .setOngoing(true)
            .build()


    companion object {
        const val NOTIFICATION_ID = 999
        private const val NOTIFICATION_MISSION_ID = 100
    }
}