package dev.gerits.speed.ui.overview

import androidx.compose.animation.core.Ease
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.adaptive.currentWindowSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import dev.gerits.speed.data.location.Location
import dev.gerits.speed.ui.component.compas.Compass
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
        val maxSpeed by viewModel.maxSpeed.collectAsStateWithLifecycle()

        when (uiState) {
            is Success -> OverviewScreen(
                location = (uiState as Success).location,
                orientation = (uiState as Success).orientation,
                maxSpeed = maxSpeed,
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun OverviewScreen(
    location: Location,
    orientation: Float,
    maxSpeed: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val matchHeightConstraintsFirst = currentWindowSize().height < currentWindowSize().width

        Gauge(
            modifier = Modifier
                .padding(32.dp)
                .aspectRatio(1f, matchHeightConstraintsFirst)
                .fillMaxWidth(),
            speed = location.speed
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,

                ) {
                Compass(
                    modifier = Modifier
                        .padding(end = 24.dp)
                        .size(96.dp),
                    orientation = orientation
                )
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "TOTAL DISTANCE:",
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "12 km",
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                VerticalDivider(
                    Modifier
                        .height(64.dp)
                        .padding(horizontal = 8.dp)
                )
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "MAX SPEED:",
                        fontSize = MaterialTheme.typography.titleSmall.fontSize,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "%.${0}f km/h".format(maxSpeed),
                        fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun OverviewPreview() {
    OverviewScreen(
        location = Location(1.0, 2.0, 3.0, 50.0f, 5.0f),
        orientation = 120f,
        maxSpeed = 60f,
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalPermissionsApi::class)
@Composable
internal fun LocationPermissionScreen(
    modifier: Modifier = Modifier,
    state: MultiplePermissionsState
) {
    Column(
        modifier.padding(16.dp)
    ) {
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
