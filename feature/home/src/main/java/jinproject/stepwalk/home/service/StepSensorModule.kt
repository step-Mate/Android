package jinproject.stepwalk.home.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import jinproject.stepwalk.home.utils.onKorea
import jinproject.stepwalk.home.worker.StepInsertWorker
import java.time.LocalDateTime

internal class StepSensorModule(
    private val context: Context,
    currentTotalStep: Long,
    onSensorChanged: (Long) -> Unit
) {
    private val sensorManager: SensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    var stepCounterTotal = currentTotalStep
    private var stepCounterLast = currentTotalStep
    private var startTime: LocalDateTime = LocalDateTime.now()
    private var endTime: LocalDateTime = LocalDateTime.now()

    private val stepListener: SensorEventListener by lazy {
        object : SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {
                onSensorChanged(stepCounterTotal++)
                setWorkOnStep()
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

        }
    }

    init {
        registerSensor()
    }

    private fun registerSensor() {
        sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun unRegisterSensor() {
        sensorManager.unregisterListener(stepListener, stepSensor)
    }

    private fun setWorkOnStep() {
        when {
            endTime.onKorea().dayOfMonth != LocalDateTime.now().onKorea().dayOfMonth -> {
                startTime = LocalDateTime.now()
                endTime = LocalDateTime.now()
                stepCounterTotal = 1
                stepCounterLast = 0
            }

            (LocalDateTime.now().onKorea().toEpochSecond() - endTime.onKorea().toEpochSecond()) < 60 -> {
                endTime = LocalDateTime.now()
            }

            else -> {
                val workRequest = OneTimeWorkRequestBuilder<StepInsertWorker>()
                    .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                    .setInputData(
                        Data
                            .Builder()
                            .putLong("distance", getStepOneTime())
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

    private fun getStepOneTime(): Long = kotlin.run {
        val step = stepCounterTotal - stepCounterLast
        stepCounterLast = stepCounterTotal

        step
    }
}