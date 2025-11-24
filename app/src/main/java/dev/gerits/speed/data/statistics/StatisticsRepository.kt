package dev.gerits.speed.data.statistics

import android.location.Location
import androidx.datastore.core.DataStore
import dev.gerits.speed.data.location.LocationDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface StatisticsRepository {
    fun maxSpeedFlow(): Flow<Float>
    fun totalDistanceFlow(): Flow<Float>
}

@Singleton
class DefaultStatisticsRepository @Inject constructor(
    private val statisticsDataStore: DataStore<Statistics>,
    private val locationDataSource: LocationDataSource,
    applicationScope: CoroutineScope,
) : StatisticsRepository {

    init {
        applicationScope.launch {
            locationDataSource.getLocationUpdates()
                .collect { location ->
                    tryUpdateMaxSpeed(location.speed)
                }
        }

        applicationScope.launch {
            locationDataSource.getLocationUpdates()
                .filter { location -> location.accuracy < 10f }
                .scan(null as Location?) { lastLocation, newLocation ->
                    lastLocation?.let {
                        increaseMaxDistance(it.distanceTo(newLocation))
                    }
                    newLocation
                }.collect {}
        }
    }

    override fun maxSpeedFlow(): Flow<Float> = statisticsDataStore.data.map { statistics ->
        statistics.maxSpeed
    }

    override fun totalDistanceFlow(): Flow<Float> = statisticsDataStore.data.map { statistics ->
        statistics.totalDistance
    }

    private suspend fun tryUpdateMaxSpeed(maxSpeed: Float) {
        statisticsDataStore.updateData { statistics ->
            when (statistics.maxSpeed < maxSpeed) {
                true -> statistics.copy(maxSpeed = maxSpeed)
                else -> statistics
            }
        }
    }

    private suspend fun increaseMaxDistance(distance: Float) {
        statisticsDataStore.updateData { statistics ->
            statistics.copy(totalDistance = statistics.totalDistance + distance)
        }
    }
}