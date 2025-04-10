package com.example.weatherseer

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherseer.ui.theme.WeatherSeerTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

// Default/Starter Zipcode
var zipcode: String = "55155"

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var locationRequired: Boolean = false

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        if (locationRequired) {
            startLocationUpdates()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        locationCallback.let {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 100
            )
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(3000)
                .setMaxUpdateDelayMillis(100)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                it,
                Looper.getMainLooper()
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onPause() {
        super.onPause()
        locationCallback.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewModels for current data and forecast data
        val weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        val forecastViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        val context = applicationContext
        val currentScreen = context.getString(R.string.currentScreen)
        val forecastScreen = context.getString(R.string.forecastScreen)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        setContent {

            var latitude by remember { mutableDoubleStateOf(0.0) }
            var longitude by remember { mutableDoubleStateOf(0.0) }

            locationCallback = object: LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for (location in p0.locations) {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                }
            }

            WeatherSeerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        weatherViewModel,
                        forecastViewModel,
                        currentScreen,
                        forecastScreen,
                        latitude,
                        longitude
                    ) { startLocationUpdates() }
                }
            }
        }
    }



    // Navigation
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @Composable
    fun AppNavigation(
        currentViewModel: WeatherViewModel,
        forecastViewModel: WeatherViewModel,
        currentScreen: String,
        forecastScreen: String,
        lat: Double,
        lon: Double,
        startLocationUpdates: () -> Unit,

        ) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = currentScreen) {
            composable(currentScreen) {
                FirstScreen(
                    viewModel = currentViewModel,
                    onNavigateForecastClicked = {
                        navController.navigate(forecastScreen)
                    },
                    lat = lat,
                    lon = lon,
                    startLocationUpdates
                )
            }
            composable(forecastScreen) {
                ForecastScreen(
                    forecastViewModel,
                    zipcode,
                    onNavigateBackClicked = { navController.popBackStack()
                    },
                    lat = lat,
                    lon = lon,
                    startLocationUpdates
                )
            }
        }
    }
}

