package dev.gerits.speed.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.gerits.speed.data.location.Location
import dev.gerits.speed.data.location.LocationRepository
import dev.gerits.speed.data.sensor.SensorRepository
import dev.gerits.speed.data.statistics.StatisticsRepository
import dev.gerits.speed.ui.overview.OverviewUiState.Loading
import dev.gerits.speed.ui.overview.OverviewUiState.Success
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val sensorRepository: SensorRepository,
    private val statisticsRepository: StatisticsRepository
) : ViewModel() {

    val uiState: StateFlow<OverviewUiState> =
        locationRepository.listenToLocation
            .distinctUntilChanged()
            .onEach { updateMaxSpeed(it.speed) }
            .combine(sensorRepository.listenToOrientation) { location, orientation -> location to orientation }
            .map { Success(location = it.component1(), orientation = it.component2()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

    val maxSpeed: StateFlow<Float> =
        statisticsRepository.maxSpeedFlow()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0f)

    fun updateMaxSpeed(maxSpeed: Float) {
        viewModelScope.launch {
            statisticsRepository.tryUpdateMaxSpeed(maxSpeed)
        }
    }

}

sealed interface OverviewUiState {
    object Loading : OverviewUiState
    data class Success(val location: Location, val orientation: Float) : OverviewUiState
}