package com.example.weatherseer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherseer.ui.theme.WeatherSeerTheme

const val CURRENTSCREEN = "first_screen"
const val FORECASTSCREEN = "forecast_screen"
var zipcode: String = ""

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ViewModels for current data and forecast data
        val weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        val forecastViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        setContent {
            WeatherSeerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(weatherViewModel, forecastViewModel)
                }
            }
        }
    }
}

// Navigation
@Composable
fun AppNavigation(
    currentViewModel: WeatherViewModel,
    forecastViewModel: WeatherViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = CURRENTSCREEN) {
        composable(CURRENTSCREEN) {
            FirstScreen(
                viewModel = currentViewModel,
                onNavigateForecastClicked = {
                    navController.navigate(FORECASTSCREEN)
                }
            )
        }
        composable(FORECASTSCREEN) {
            ForecastScreen(forecastViewModel, zipcode, onNavigateBackClicked= { navController.popBackStack() })
        }
    }
}