package dev.gerits.speed.data

import dev.gerits.speed.data.source.SensorDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface SensorRepository {
    val listenToOrientation: Flow<Float>
}

@Singleton
class DefaultSensorRepository @Inject constructor(
    SensorDataSource: SensorDataSource
) : SensorRepository {
    override val listenToOrientation: Flow<Float> = SensorDataSource.getOrientationUpdates()
}
