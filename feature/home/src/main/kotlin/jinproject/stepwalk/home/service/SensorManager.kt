package jinproject.stepwalk.home.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

internal class StepSensorManager(
    context: Context,
    onSensorChanged: (SensorEvent?) -> Unit
) {
    private val sensorManager: SensorManager by lazy { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val stepSensor: Sensor? by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER) }

    private val stepListener: SensorEventListener =
        object : SensorEventListener {
            override fun onSensorChanged(p0: SensorEvent?) {
                onSensorChanged(p0)
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

        }

    fun registerSensor() {
        sensorManager.registerListener(stepListener, stepSensor, SensorManager.SENSOR_DELAY_UI)
    }

    fun unRegisterSensor() {
        sensorManager.unregisterListener(stepListener, stepSensor)
    }
}