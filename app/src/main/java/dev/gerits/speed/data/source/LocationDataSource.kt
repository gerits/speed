package dev.gerits.speed.data.source

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.gerits.speed.data.Location
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

interface LocationDataSource {
    fun getLocationUpdates(): Flow<Location>
}

@Singleton
class DefaultLocationDataSource @Inject constructor(
    @param:ApplicationContext val context: Context
) : LocationDataSource {

    private val client: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<Location> {
        val request = LocationRequest.Builder(10L)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setGranularity(Granularity.GRANULARITY_FINE)
            .build()

        return callbackFlow {
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.lastLocation?.let {
                        launch { send(toLocation(it)) }
                    }
                }
            }
            client.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())

            awaitClose {
                client.removeLocationUpdates(locationCallback)
            }
        }
    }

    private fun toLocation(location: android.location.Location): Location = Location(
        location.latitude,
        location.longitude,
        location.altitude,
        location.speed,
        location.bearing
    )

}

//class LocationDataSource(
//    private val client: FusedLocationProviderClient
//) {
//    val latestLocation: Flow<List<Location>> = flow {
//        client.lastLocation.addOnSuccessListener { location ->
//            location?.let {
//                // Not a flow, but a one-time operation
//            }
//        }
//    }.let {
//        callbackFlow {
//            val locationRequest = LocationRequest.create()
//            val callback = object : LocationCallback() {
//                override fun onLocationResult(result: LocationResult) {
//                    trySend(result.locations.map { Location(it.latitude, it.longitude) })
//                }
//            }
//            client.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
//                .addOnFailureListener { e -> close(e) }
//
//            awaitClose {
//                client.removeLocationUpdates(callback)
//            }
//        }
//    }
//}