package dev.gerits.speed.data

import dev.gerits.speed.data.source.LocationDataSource
import kotlinx.coroutines.flow.Flow
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
}

