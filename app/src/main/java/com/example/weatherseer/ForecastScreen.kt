package com.example.weatherseer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp




@Composable
fun ForecastScreen(viewModel: WeatherViewModel, zip: String, onNavigateBackClicked: () -> Unit) {

    // Fetch Weather Data
    viewModel.GetQueryInfo()
    viewModel.getForecastData(zip)
    val forecastResult = viewModel.forecastResult.observeAsState()

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp)
    ) {
        Button(onClick = {
            when (forecastResult.value) {
                is NetworkResponse.Error -> {
                    zipcode = "55155";
                    onNavigateBackClicked();
                }
                is NetworkResponse.Success -> onNavigateBackClicked()
                null -> {}
            } },
            colors = ButtonColors(
            contentColor = Color.Black,
            containerColor = Color.Blue,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Yellow
        ),
        ){
            Text("Back")
        }

        // Evaluate Forecast Data
        when(val result = forecastResult.value) {
            is NetworkResponse.Error -> {
                Text(text = result.message)
            }
            is NetworkResponse.Success -> {
                ForecastColumn(result.data.city.name,result.data.city.country, result.data.list)
            }
            null -> {}
        }
    }

}

////////// Add the cityCountry function, Display all the necessary info, fix design
//////// Get icon images and match them up with Icon id to display them correctly

@Composable
fun ForecastColumn(city: String, country: String, forecastList: List<ForecastList>, ) {
    Column(){
        forecastList.let { items ->
            LazyColumn {
                items(count = forecastList.size) { item ->
                    Box(
                        modifier = Modifier
                    ) {
                        Text(forecastList[item].temp?.day.toString())
                        }
                    }
                }
            }
        }
    }
