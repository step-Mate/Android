package jinproject.stepwalk.home.service

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.R
import jinproject.stepwalk.home.utils.StepWalkChannelId
import jinproject.stepwalk.home.utils.onKorea
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

class StepService : LifecycleService() {

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var stepCounter = 0L
    private val stepListener: SensorEventListener by lazy {
        object : SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {
                stepNotiLayout?.setTextViewText(R.id.tv_stepHeader,stepCounter++.toString())
                startForeground(999,notification?.build())
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

        }
    }
    @Inject lateinit var healthConnector: HealthConnector
    private var stepNotiLayout: RemoteViews? = null
    private var notification: NotificationCompat.Builder? = null

    override fun onCreate() {
        super.onCreate()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_FASTEST)

        setNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        val inputData = intent?.getBooleanExtra("insertStep",false) ?: false
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
                    healthConnector.insertSteps(
                        step = stepCounter,
                        startTime = instant,
                        endTime = instant
                            .plus(59, ChronoUnit.MINUTES)
                    )
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(stepListener, stepSensor)
        stepSensor = null
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

        stepNotiLayout = RemoteViews(packageName,R.layout.step_notification)

        stepNotiLayout?.setTextViewText(R.id.tv_stepHeader,"0")

        notification =
            NotificationCompat.Builder(this, StepWalkChannelId)
                .setSmallIcon(jinproject.stepwalk.design.R.drawable.ic_person_walking)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(stepNotiLayout)
                .setCustomBigContentView(stepNotiLayout)
                .addAction(jinproject.stepwalk.design.R.drawable.ic_x,"끄기",exitPendingIntent)


        startForeground(999, notification?.build())
    }
}