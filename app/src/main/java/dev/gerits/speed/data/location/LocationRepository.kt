package dev.gerits.speed.data.location

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

