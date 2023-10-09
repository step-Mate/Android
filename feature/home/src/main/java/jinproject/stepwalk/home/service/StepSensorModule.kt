package jinproject.stepwalk.home.service

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import jinproject.stepwalk.home.receiver.AlarmReceiver
import jinproject.stepwalk.home.utils.setInExactRepeating
import jinproject.stepwalk.home.utils.onKorea
import jinproject.stepwalk.home.worker.StepInsertWorker
import java.time.LocalDateTime
import java.util.Calendar

internal class StepSensorModule(
    private val context: Context,
    private val steps: SnapshotStateList<Long>,
    onSensorChanged: (Long) -> Unit
) {
    private val sensorManager: SensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val alarmManager: AlarmManager by lazy { context.getSystemService(Context.ALARM_SERVICE) as AlarmManager }

    private var startTime: LocalDateTime = LocalDateTime.now()
    private var endTime: LocalDateTime = LocalDateTime.now()

    private val stepListener: SensorEventListener by lazy {
        object : SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {
                val step = p0?.values?.first()?.toLong() ?: 0L

                val today = when (step) {
                    0L -> {
                        steps.apply {
                            removeLast()
                            add(0L)
                        }

                        steps.first() + 1L
                    }

                    else -> {
                        when {
                            steps.first() == 0L -> {
                                steps.removeLast()
                                steps.add(step)
                            }
                        }
                        step - steps.last()
                    }
                }

                Log.d("test","v: $step c: ${steps.first()} y: ${steps.last()}")

                onSensorChanged(today)
                setWorkOnStep(today)
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

        }
    }

    init {
        registerSensor()
        alarmUpdatingLastStep()
    }

    private fun alarmUpdatingLastStep() {
        val time = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setInExactRepeating(
            context = context,
            notifyIntent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("lastStep", steps.last())
            },
            type = AlarmManager.RTC_WAKEUP,
            time = time.timeInMillis,
            interval = AlarmManager.INTERVAL_DAY
        )
    }

    private fun registerSensor() {
        sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun unRegisterSensor() {
        sensorManager.unregisterListener(stepListener, stepSensor)
    }

    private fun setWorkOnStep(todayStep: Long) {
        when {
            endTime.onKorea().dayOfMonth != LocalDateTime.now().onKorea().dayOfMonth -> {
                startTime = LocalDateTime.now()
                endTime = LocalDateTime.now()
            }

            (LocalDateTime.now().onKorea().toEpochSecond() - endTime.onKorea()
                .toEpochSecond()) < 60 -> {
                endTime = LocalDateTime.now()
            }

            else -> {
                val workRequest = OneTimeWorkRequestBuilder<StepInsertWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setInputData(
                        Data
                            .Builder()
                            .putLong("distance", todayStep)
                            .putLong("start", startTime.onKorea().toEpochSecond())
                            .putLong("end", endTime.onKorea().toEpochSecond())
                            .build()
                    )
                    .build()

                WorkManager
                    .getInstance(context)
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
}