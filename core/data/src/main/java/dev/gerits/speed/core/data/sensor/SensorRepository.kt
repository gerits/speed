package dev.gerits.speed.core.data.sensor

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface SensorRepository {
    val listenToOrientation: Flow<Float>
}

@Singleton
class DefaultSensorRepository @Inject constructor(
    sensorDataSource: SensorDataSource
) : SensorRepository {
    override val listenToOrientation: Flow<Float> = sensorDataSource.getOrientationUpdates()
}
