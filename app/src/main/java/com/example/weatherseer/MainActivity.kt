package com.example.weatherseer

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weatherseer.ui.theme.WeatherSeerTheme

// Default/Starter Zipcode
var zipcode: String = "55155"

class MainActivity : ComponentActivity() {
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

        setContent {
            WeatherSeerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        weatherViewModel,
                        forecastViewModel,
                        currentScreen,
                        forecastScreen
                    )
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
        forecastScreen: String
    ) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = currentScreen) {
            composable(currentScreen) {
                FirstScreen(
                    viewModel = currentViewModel,
                    onNavigateForecastClicked = {
                        navController.navigate(forecastScreen)
                    }
                )
            }
            composable(forecastScreen) {
                ForecastScreen(
                    forecastViewModel,
                    zipcode,
                    onNavigateBackClicked = { navController.popBackStack() }
                )
            }
        }
    }
}
