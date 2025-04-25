package com.example.weatherseer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun AppNavigation(
    navController: NavHostController,
    currentViewModel: WeatherViewModel,
    forecastViewModel: WeatherViewModel,
    currentScreen: String,
    forecastScreen: String,
    lat: Double,
    lon: Double,
    startLocationUpdates: () -> Unit,

    ) {

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