package dev.gerits.speed.data.location

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

interface LocationRepository {
    val listenToLocation: Flow<Location>
}

@Singleton
class DefaultLocationRepository @Inject constructor(
    locationDataSource: LocationDataSource
) : LocationRepository {
    override val listenToLocation: Flow<Location> = locationDataSource.getLocationUpdates()
        .map { toLocation(it) }

    private fun toLocation(location: android.location.Location): Location = Location(
        location.latitude,
        location.longitude,
        location.altitude,
        location.speed,
        location.bearing
    )
}

