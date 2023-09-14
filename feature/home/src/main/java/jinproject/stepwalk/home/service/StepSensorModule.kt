package jinproject.stepwalk.home.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

internal class StepSensorModule(
    private val context: Context,
    step: Long,
    onSensorChanged: (Long) -> Unit
) {
    private val sensorManager: SensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private var stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    var stepCounterTotal = step
    private var stepCounterLast = step

    private val stepListener: SensorEventListener by lazy {
        object : SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {
                onSensorChanged(stepCounterTotal++)
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

    fun getStepOnThisHour(): Long = kotlin.run {
        val step = stepCounterTotal - stepCounterLast
        stepCounterLast = stepCounterTotal

        step
    }
}