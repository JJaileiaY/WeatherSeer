package com.example.weatherseer

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat

@Composable
fun ForecastScreen(
    viewModel: WeatherViewModel,
    zip: String,
    onNavigateBackClicked: () -> Unit,
    lat: Double,
    lon: Double,
    startLocationUpdates: () -> Unit)
{
    // Fetch Weather Data
    viewModel.GetQueryInfo()
    val forecastResult: State<NetworkResponse<ForecastMetaData>?>

    val context = LocalContext.current

    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
    == PackageManager.PERMISSION_GRANTED && zipcode == "") {

        startLocationUpdates()
        viewModel.getForecastData(lat, lon)
        forecastResult = viewModel.forecastResult.observeAsState()
    }
    else {
        viewModel.getForecastData(zip)
        forecastResult = viewModel.forecastResult.observeAsState()
    }

    // Create Alert Dialog if zipcode not found
    var showZipNotFound by remember { mutableStateOf(false) }
    if (showZipNotFound) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.alertTitle)) },
            text = { Text(stringResource(R.string.alertMessage)) },
            confirmButton = {
                Button(onClick = {
                    zipcode = context.getString(R.string.defaultZip)
                    showZipNotFound = false
                    onNavigateBackClicked()
                }) {
                    Text(stringResource(R.string.alertOk))
                }
            },
            dismissButton = null
        )
    }

    // Start of Forecast UI
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF640baa), Color(0xFF8f149e))))
    ) {
        Box {
            AppTitle()
            // Back Button
            Button(
                onClick = {
                    when (forecastResult.value) {
                        is NetworkResponse.Error -> {
                            zipcode = context.getString(R.string.defaultZip)
                            onNavigateBackClicked()
                        }
                        is NetworkResponse.Success -> {
                            onNavigateBackClicked()
                        }
                        null -> {}
                    }
                },
                colors = ButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Magenta,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp)
            ) {
                Text(stringResource(R.string.back))
            }
        }
        // 16 Forecast Title
        Column (
            modifier = Modifier
            .fillMaxWidth()
        ) {
            Text(stringResource(R.string.forecastTitle),
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 20.dp)
            )
        }

        // Evaluate Forecast Data
        when(val result = forecastResult.value) {
            is NetworkResponse.Error -> {
                showZipNotFound = true
            }
            is NetworkResponse.Success -> {
                ForecastColumn(result.data.city.name, result.data.city.country, result.data.list)
            }
            null -> {}
        }
    }
}

// Create the main view of the page
@Composable
fun ForecastColumn(city: String, country: String, forecastList: List<ForecastList>) {
    Column(
        modifier = Modifier
            .padding(top = 25.dp)
            .padding(horizontal = 40.dp)
    ) {
        // Create the City and Country
        CityState(city, country)

        // Create the LazyColumn of Forecasts
        forecastList.let { items ->
            LazyColumn {
                items(count = forecastList.size) { item ->
                    Box (
                        modifier = Modifier
                            .width(340.dp)
                            .height(120.dp)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x60322390))
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                        ) {
                            // Put the Icon, Temp, Description, and Details in the row
                            ForecastIcon(forecastList[item].weather[0].icon)
                            ForecastTemp(forecastList[item].temp?.day, forecastList[item].feelsLike?.day, forecastList[item].temp?.night)
                            ForecastDesc(forecastList[item].weather[0].description)
                            ForecastDetails(forecastList[item].humidity)
                        }
                    }
                }
            }
        }
    }
}

// Create the Icon in the forecast column
@Composable
fun ForecastIcon(icon: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally)
    {
        Image(
            when (icon) {
                "01d", "01n" -> painterResource(R.drawable.sunny)
                "02d", "02n" -> painterResource(R.drawable.fewclouds)
                "03d", "03n" -> painterResource(R.drawable.scatclouds)
                "04d", "04n" -> painterResource(R.drawable.brokenclouds)
                "09d", "09n" -> painterResource(R.drawable.showerrain)
                "10d", "10n" -> painterResource(R.drawable.rain)
                "11d", "11n" -> painterResource(R.drawable.storm)
                "13d", "13n" -> painterResource(R.drawable.snow)
                "50d", "50n" -> painterResource(R.drawable.mist)
                else -> {
                    painterResource(R.drawable.sunny)
                } },
            contentDescription = when (icon) {
                "01d", "01n" -> stringResource(R.string.sunny)
                "02d", "02n" -> stringResource(R.string.fewclouds)
                "03d", "03n" -> stringResource(R.string.scatclouds)
                "04d", "04n" -> stringResource(R.string.brokenclouds)
                "09d", "09n" -> stringResource(R.string.showerrain)
                "10d", "10n" -> stringResource(R.string.rain)
                "11d", "11n" -> stringResource(R.string.storm)
                "13d", "13n" -> stringResource(R.string.snow)
                "50d", "50n" -> stringResource(R.string.mist)
                else -> {
                    stringResource(R.string.sunny)
                } },
            modifier = Modifier
                .height(60.dp)
                .width(60.dp)
                .padding(start = 15.dp)
                .padding(top = 25.dp)
        )
    }
}

// Create the Temperature and FeelsLike info in the forecast column
@Composable
fun ForecastTemp(temp: Double?, feelsTemp: Double?, tempN: Double?) {
    Column(
        modifier = Modifier.padding(15.dp))
    {
        Row {
            Text(
                text = temp?.toInt().toString() + stringResource(R.string.degree),
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = stringResource(R.string.slash) + tempN?.toInt().toString() + stringResource(R.string.degree),
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Bottom)
            )
        }
        Row {
            Text(
                text = stringResource(R.string.feelsTemp) + feelsTemp?.toInt().toString() + stringResource(R.string.degree),
                color = Color.White,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

// Create the Weather Description in the forecast column
@Composable
fun ForecastDesc(desc: String) {
    Column {
        Row(modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .width(90.dp)
        ) {
            Text(
                text = desc.capitalizeFirstLetter(),
                color = Color.White,
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(15.dp)
                    .padding(top = 10.dp)
            )
        }
    }
}

// Create the other temperature details in the forecast column
@Composable
fun ForecastDetails(humidity: Int?) {
    Column {
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(
                text = humidity.toString() + stringResource(R.string.percent) + stringResource(R.string.humidity),
                color = Color.White,
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 10.dp)
                    .padding(top = 20.dp)
            )
        }
    }
}

// Extension function for String, to capitalize first character.
fun String.capitalizeFirstLetter(): String {
    return if (isNotEmpty()) {
        val first = this[0].uppercase()
        val restString = substring(1)
        first + restString
    } else {
        this
    }
}