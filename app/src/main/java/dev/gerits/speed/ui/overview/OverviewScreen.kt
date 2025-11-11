package dev.gerits.speed.ui.overview

import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.gerits.speed.data.Location
import dev.gerits.speed.ui.component.gauge.Gauge
import dev.gerits.speed.ui.overview.OverviewUiState.Loading
import dev.gerits.speed.ui.overview.OverviewUiState.Success

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OverviewScreen(
    modifier: Modifier = Modifier,
    viewModel: OverviewViewModel = hiltViewModel()
) {
    val locationPermissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    )

    if (locationPermissionState.allPermissionsGranted) {
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        when (uiState) {
            is Success -> OverviewScreen(
                location = (uiState as Success).location,
                modifier = modifier
            )

            is Loading -> LoadingScreen(modifier = modifier)
        }
    } else {
        LocationPermissionScreen(
            modifier = modifier,
            state = locationPermissionState
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun OverviewScreen(
    location: Location,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Gauge(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth(), speed = location.speed
        )
    }
}

@Preview
@Composable
fun OverviewPreview() {
    OverviewScreen(location = Location(1.0, 2.0, 3.0, 50.0f, 5.0f))
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalPermissionsApi::class)
@Composable
internal fun LocationPermissionScreen(
    modifier: Modifier = Modifier,
    state: MultiplePermissionsState
) {
    Column(modifier.padding(16.dp)) {
        Column {
            val textToShow = if (state.shouldShowRationale) {
                "The location is important for this app. Please grant the permission."
            } else {
                "Location permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = { state.launchMultiplePermissionRequest() }) {
                Text("Request permission")
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview
@Composable
fun LocationPermissionPreview() {
    LocationPermissionScreen(
        state = rememberMultiplePermissionsState(
            listOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite")
    val speed by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = Ease),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )


    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box {
            Gauge(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                speed = speed,
                maximum = 100f,
                showText = false
            )
        }
    }
}

@Preview
@Composable
fun LoadingPreview() {
    LoadingScreen()
}
