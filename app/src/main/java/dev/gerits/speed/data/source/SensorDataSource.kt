package dev.gerits.speed.data.source

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface SensorDataSource {
    fun getOrientationUpdates(): Flow<Float>
}

@Singleton
class DefaultSensorDataSource @Inject constructor(
    @param:ApplicationContext val context: Context
) : SensorDataSource {

    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

//    private val accelerometerReading = FloatArray(3)
//    private val magnetometerReading = FloatArray(3)

    override fun getOrientationUpdates(): Flow<Float> = callbackFlow {
        val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (rotationVectorSensor == null) {
            close(IllegalStateException("Rotation Vector sensor is not available on this device."))
            return@callbackFlow
        }

        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)

        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    // Get the rotation matrix from the rotation vector sensor
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                    // Get the orientation angles from the rotation matrix
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)

                    // The azimuth is the first value (index 0), converted to degrees
                    var azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                    if (azimuth < 0) {
                        azimuth += 360 // Normalize to a 0-360 range
                    }

                    // Emit the new azimuth value
                    launch {
                        send(azimuth)
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // You can use this to provide user feedback if accuracy is low
                // e.g., if accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE
            }
        }

        // Register the rotation vector sensor listener
        sensorManager.registerListener(
            sensorListener,
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_UI
        )

        awaitClose {
            sensorManager.unregisterListener(sensorListener)
        }

//        val rotationMatrix = FloatArray(9)
//        val orientationAngles = FloatArray(3)
//
//        val sensorEventListener = object : SensorEventListener {
//            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
//            }
//
//            override fun onSensorChanged(event: SensorEvent?) {
//                if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
//                    System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
//                } else if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD) {
//                    System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
//                }
//
//                SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
//                SensorManager.getOrientation(rotationMatrix, orientationAngles)
//
//                var azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
//                if (azimuth < 0f) {
//                    azimuth += 360f
//                }
//
//                launch {
//                    send(azimuth)
//                }
//            }
//        }
//
//
//        sensorManager.registerListener(
//            sensorEventListener,
//            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
//            SensorManager.SENSOR_DELAY_UI
//        )
//        sensorManager.registerListener(
//            sensorEventListener,
//            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
//            SensorManager.SENSOR_DELAY_UI
//        )
//
//        awaitClose {
//            sensorManager.unregisterListener(sensorEventListener)
//        }
    }
}
