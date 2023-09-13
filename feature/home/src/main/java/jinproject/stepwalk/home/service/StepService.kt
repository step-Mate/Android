package jinproject.stepwalk.home.service

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import jinproject.stepwalk.domain.model.METs
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.R
import jinproject.stepwalk.home.utils.StepWalkChannelId
import jinproject.stepwalk.home.utils.onKorea
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@AndroidEntryPoint
internal class StepService : LifecycleService() {

    @Inject
    lateinit var healthConnector: HealthConnector

    private lateinit var sensorModule: StepSensorModule
    private var stepNotiLayout: RemoteViews? = null
    private var notification: NotificationCompat.Builder? = null

    override fun onCreate() {
        super.onCreate()

        lifecycleScope.launch {
            sensorModule = StepSensorModule(
                context = this@StepService,
                step = healthConnector.getTotalStepToday(METs.Walk),
                onSensorChanged = { stepCounter ->
                    stepNotiLayout?.setTextViewText(R.id.tv_stepHeader, stepCounter.toString())
                    startForeground(999, notification?.build())
                }
            )

            setNotification()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val inputData = intent?.getBooleanExtra("insertStep", false) ?: false
        val exit = intent?.getBooleanExtra("exit", false) ?: false

        when {
            exit -> {
                stopSelf()
            }

            inputData -> {
                val instant = Instant
                    .now()
                    .onKorea()
                    .truncatedTo(ChronoUnit.HOURS)
                    .toInstant()

                lifecycleScope.launch {
                    if (::healthConnector.isInitialized && ::sensorModule.isInitialized) {
                        healthConnector.insertSteps(
                            step = sensorModule.getStepOnThisHour(),
                            startTime = instant,
                            endTime = instant
                                .plus(59, ChronoUnit.MINUTES)
                        )
                    } else {
                        Log.d("test", "not initalized")
                    }
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
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
                .setSmallIcon(jinproject.stepwalk.design.R.drawable.ic_person_walking)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(stepNotiLayout)
                .setCustomBigContentView(stepNotiLayout)
                .addAction(jinproject.stepwalk.design.R.drawable.ic_x, "끄기", exitPendingIntent)


        startForeground(999, notification?.build())
    }
}