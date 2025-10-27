package dev.gerits.speed.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import dev.gerits.speed.ui.theme.SpeedTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SpeedTheme {
                MainNavigation()
            }
        }
    }

}


//@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalPermissionsApi::class)
//class MainActivity : ComponentActivity() {
//    private lateinit var fusedLocationClient: FusedLocationProviderClient
//    internal lateinit var locationLister: LocationListener
//
//    private var gaugeViewModel = GaugeViewModel()
//    private var compasViewModel = CompasViewModel()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        locationLister = LocationListener { location ->
//            gaugeViewModel.updateConnected(location.hasSpeed())
//            gaugeViewModel.updateSpeed(location.speed * 3.6f)
//            compasViewModel.updateDirection(location.bearing)
//        }
//
//        setContent {
//            val currentView = LocalView.current
//            DisposableEffect(Unit) {
//                currentView.keepScreenOn = true
//                onDispose {
//                    currentView.keepScreenOn = false
//                }
//            }
//
//            SpeedTheme {
//                Surface(
//                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
//                ) {
//                    val locationPermissionState = rememberPermissionState(
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    )
//
//                    LaunchedEffect(locationPermissionState.status) {
//                        if (!locationPermissionState.status.isGranted) {
//                            locationPermissionState.launchPermissionRequest()
//                        }
//                    }
//
//                    if (locationPermissionState.status.isGranted) {
//                        Speed(gaugeViewModel, compasViewModel, mainActivity = this)
//                    } else {
//                        Column(
//                            modifier = Modifier.fillMaxSize(),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text(
//                                "Location permission required for this feature to be available. " +
//                                        "Please grant the permission.",
//                                modifier = Modifier.padding(16.dp),
//                                textAlign = TextAlign.Center,
//                                fontWeight = FontWeight.Bold
//                            )
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        stopLocationUpdates()
//    }
//
//    @Throws(SecurityException::class)
//    internal fun startLocationUpdates(listener: LocationListener) {
//        val locationRequest =
//            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
//                .setWaitForAccurateLocation(true)
//                .setMinUpdateIntervalMillis(50)
//                .setMaxUpdateDelayMillis(100)
//                .build()
//        fusedLocationClient.requestLocationUpdates(
//            locationRequest,
//            listener,
//            Looper.getMainLooper()
//        )
//    }
//
//    internal fun stopLocationUpdates() {
//        fusedLocationClient.removeLocationUpdates(locationLister)
//    }
//
//}
//
//@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalPermissionsApi::class)
//@Composable
//fun Speed(
//    gaugeViewModel: GaugeViewModel,
//    compasViewModel: CompasViewModel,
//    mainActivity: MainActivity,
//    modifier: Modifier = Modifier
//) {
//    BoxWithConstraints(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        val locationPermissionState = rememberPermissionState(
//            Manifest.permission.ACCESS_FINE_LOCATION
//        )
//
//        LaunchedEffect(locationPermissionState.status) {
//            if (!locationPermissionState.status.isGranted) {
//                locationPermissionState.launchPermissionRequest()
//            }
//        }
//
//        DisposableEffect(mainActivity) {
//            mainActivity.startLocationUpdates(mainActivity.locationLister)
//            onDispose {
//                mainActivity.stopLocationUpdates()
//            }
//        }
//
//        // Use available width or height to create a responsive layout
//        val isPortrait = maxHeight > maxWidth
//        val contentModifier = if (isPortrait) {
//            Modifier.fillMaxWidth()
//        } else {
//            Modifier.fillMaxHeight()
//        }
//
//        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Box(
//                modifier = contentModifier.weight(2f),
//                contentAlignment = Alignment.Center
//            ) {
//                Gauge(
//                    maximum = 10f,
//                    gaugeViewModel = gaugeViewModel,
//                    modifier = Modifier.fillMaxSize()
//                )
//                SpeedText(gaugeViewModel = gaugeViewModel)
//            }
//            Box(
//                modifier = contentModifier.weight(1f),
//                contentAlignment = Alignment.Center
//            ) {
//                Compas(
//                    maximum = 10f,
//                    compasViewModel = compasViewModel,
//                    modifier = Modifier.size(150.dp)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun SpeedText(gaugeViewModel: GaugeViewModel, modifier: Modifier = Modifier) {
//    val speedTextStyle = SpanStyle(
//        color = MaterialTheme.colorScheme.primary,
//        fontSize = TextUnit(100f, TextUnitType.Sp),
//        fontWeight = FontWeight.ExtraBold
//    )
//    val unitTextStyle = SpanStyle(color = MaterialTheme.colorScheme.primary)
//    val errorTextStyle = SpanStyle(
//        color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.ExtraBold
//    )
//
//    val speed = gaugeViewModel.speed.collectAsState()
//    val connected = gaugeViewModel.connected.collectAsState()
//    Column(modifier = modifier) {
//        Text(
//            buildAnnotatedString {
//                if (connected.value) {
//                    withStyle(style = speedTextStyle) {
//                        append(speed.value.roundToInt().toString())
//                    }
//                    withStyle(style = unitTextStyle) {
//                        append("\nkm/h")
//                    }
//                } else {
//                    withStyle(style = errorTextStyle) {
//                        append("No GPS signal!")
//                    }
//                }
//            }, textAlign = TextAlign.Center
//        )
//    }
//}
//
//@ExperimentalMaterial3ExpressiveApi
//@Preview(showBackground = true)
//@Composable
//fun SpeedPreview() {
//    SpeedTheme(darkTheme = false, dynamicColor = false) {
//        val gaugeViewModel = GaugeViewModel()
//        gaugeViewModel.updateSpeed(20f)
//        val compasViewModel = CompasViewModel()
//        compasViewModel.updateDirection(20f)
//        Speed(gaugeViewModel, compasViewModel, mainActivity = LocalView.current.context as MainActivity)
//    }
//}