package dev.gerits.speed.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.gerits.speed.data.Location
import dev.gerits.speed.data.LocationRepository
import dev.gerits.speed.data.SensorRepository
import dev.gerits.speed.ui.overview.OverviewUiState.Loading
import dev.gerits.speed.ui.overview.OverviewUiState.Success
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OverviewViewModel @Inject constructor(
    locationRepository: LocationRepository,
    sensorRepository: SensorRepository
) : ViewModel() {

    val uiState: StateFlow<OverviewUiState> =
        locationRepository.listenToLocation
            .combine(sensorRepository.listenToOrientation) { location, orientation -> location to orientation }
            .map { Success(location = it.component1(), orientation = it.component2()) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Loading)

}

sealed interface OverviewUiState {
    object Loading : OverviewUiState
    data class Success(val location: Location, val orientation: Float) : OverviewUiState
}