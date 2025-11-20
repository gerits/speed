package dev.gerits.speed.data.statistics

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface StatisticsRepository {
    fun maxSpeedFlow(): Flow<Float>
    suspend fun tryUpdateMaxSpeed(maxSpeed: Float)
}

@Singleton
class DefaultStatisticsRepository @Inject constructor(
    val statisticsDataStore: DataStore<Statistics>
) : StatisticsRepository {

    override fun maxSpeedFlow(): Flow<Float> = statisticsDataStore.data.map { statistics ->
        statistics.maxSpeed
    }

    override suspend fun tryUpdateMaxSpeed(maxSpeed: Float) {
        statisticsDataStore.updateData { statistics ->
            when (statistics.maxSpeed < maxSpeed) {
                true -> statistics.copy(maxSpeed = maxSpeed)
                else -> statistics
            }
        }
    }
}
