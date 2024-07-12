package dev.gerits.speed

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dev.gerits.speed.model.SpeedViewModel
import dev.gerits.speed.ui.Gauge
import dev.gerits.speed.ui.theme.SpeedTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationLister: LocationListener

    private var speedViewModel = SpeedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationLister = LocationListener { location ->
            speedViewModel.updateConnected(location.hasSpeed())
            speedViewModel.updateSpeed(location.speed * 3.6f)
        }

        setContent {
            val currentView = LocalView.current
            DisposableEffect(Unit) {
                currentView.keepScreenOn = true
                onDispose {
                    currentView.keepScreenOn = false
                }
            }

            SpeedTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Speed(speedViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.Builder(100)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .setWaitForAccurateLocation(true)
                .build(),
            locationLister,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationLister)
    }

}

@Composable
fun Speed(speedViewModel: SpeedViewModel, modifier: Modifier = Modifier) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Gauge(maximum = 150, speedViewModel = speedViewModel, modifier = modifier.fillMaxSize())
        SpeedText(speedViewModel = speedViewModel, modifier.align(Alignment.Center))
    }
}

@Composable
fun SpeedText(speedViewModel: SpeedViewModel, modifier: Modifier = Modifier) {
    val speedTextStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        fontSize = TextUnit(100f, TextUnitType.Sp),
        fontWeight = FontWeight.ExtraBold
    )
    val unitTextStyle = SpanStyle(color = MaterialTheme.colorScheme.primary)
    val errorTextStyle = SpanStyle(
        color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.ExtraBold
    )

    val speed = speedViewModel.speed.collectAsState()
    val connected = speedViewModel.connected.collectAsState()
    Column(modifier = modifier) {
        Text(
            buildAnnotatedString {
                if (connected.value) {
                    withStyle(style = speedTextStyle) {
                        append(speed.value.roundToInt().toString())
                    }
                    withStyle(style = unitTextStyle) {
                        append("\nkm/h")
                    }
                } else {
                    withStyle(style = errorTextStyle) {
                        append("No GPS signal!")
                    }
                }
            }, textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SpeedPreview() {
    SpeedTheme(darkTheme = false, dynamicColor = false) {
        val speedViewModel = SpeedViewModel()
        speedViewModel.updateSpeed(20f)
        Speed(speedViewModel)
    }
}