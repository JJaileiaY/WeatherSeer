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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
        val weatherViewModel = WeatherViewModel(
            RetrofitInstance.weatherService,
            this.getString(R.string.appid),
            this.getString(R.string.units),
            this.getString(R.string.errMessage))
        val forecastViewModel = WeatherViewModel(
            RetrofitInstance.weatherService,
            this.getString(R.string.appid),
            this.getString(R.string.units),
            this.getString(R.string.errMessage))


        val currentScreen = this.getString(R.string.currentScreen)
        val forecastScreen = this.getString(R.string.forecastScreen)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setContent {

            /** LocationCallBack sometimes doesn't work anymore when switching the permissions back and forth.
                Try rerunning or resetting the emulator and wait for 1 min.
            **/

            var latitude by remember { mutableDoubleStateOf(0.0) }
            var longitude by remember { mutableDoubleStateOf(0.0) }

            locationCallback = object: LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    for (location in p0.locations) {
                        latitude = location.latitude
                        longitude = location.longitude
                    }
                    weatherViewModel.getData(latitude, longitude)
                }
            }

            val navController = rememberNavController()

            WeatherSeerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        navController,
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
}

