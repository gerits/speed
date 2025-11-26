package dev.gerits.speed.feature.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.gerits.speed.core.model.Location
import dev.gerits.speed.core.data.location.LocationRepository
import dev.gerits.speed.core.data.sensor.SensorRepository
import dev.gerits.speed.core.data.statistics.StatisticsRepository
import dev.gerits.speed.feature.overview.OverviewUiState.Loading
import dev.gerits.speed.feature.overview.OverviewUiState.Success
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    locationRepository: LocationRepository,
    sensorRepository: SensorRepository,
    statisticsRepository: StatisticsRepository
) : ViewModel() {

    val locationState: StateFlow<OverviewUiState> =
        locationRepository.listenToLocation
            .distinctUntilChanged()
            .map { Success(location = it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    val orientation: StateFlow<Float> =
        sensorRepository.listenToOrientation
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val maxSpeed: StateFlow<Float> =
        statisticsRepository.maxSpeedFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    val totalDistance: StateFlow<Float> =
        statisticsRepository.totalDistanceFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

}

sealed interface OverviewUiState {
    object Loading : OverviewUiState
    data class Success(val location: Location) : OverviewUiState
}