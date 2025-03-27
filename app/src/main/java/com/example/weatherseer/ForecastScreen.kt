package com.example.weatherseer

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ForecastScreen(viewModel: WeatherViewModel, zip: String, onNavigateBackClicked: () -> Unit) {

    // Fetch Weather Data
    viewModel.GetQueryInfo()
    viewModel.getForecastData(zip)
    val forecastResult = viewModel.forecastResult.observeAsState()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF640baa), Color(0xFF8f149e))))

    ) {
        AppTitle()
        Button(
            onClick = {
            when (forecastResult.value) {
                is NetworkResponse.Error -> {
                    zipcode = "55155";
                    onNavigateBackClicked();
                }
                is NetworkResponse.Success -> onNavigateBackClicked()
                null -> {}
            } },
            colors = ButtonColors(
            contentColor = Color.White,
            containerColor = Color.Magenta,
            disabledContentColor = Color.Transparent,
            disabledContainerColor = Color.Transparent ),
            modifier = Modifier.padding(40.dp)
        ){
            Text("Back")
        }

        // Evaluate Forecast Data
        when(val result = forecastResult.value) {
            is NetworkResponse.Error -> {
                val context = LocalContext.current
                Toast.makeText(context, "Zipcode Not Found", Toast.LENGTH_SHORT).show() }
            is NetworkResponse.Success -> {
                ForecastColumn(result.data.city.name,result.data.city.country, result.data.list)
            }
            null -> {}
        }
    }

}

////////// Display all the necessary info, fix design

@Composable
fun ForecastColumn(city: String, country: String, forecastList: List<ForecastList>, ) {
    Column(
        modifier = Modifier
            //.fillMaxWidth()
            //.padding(top = 40.dp)
    )
    {
        CityState(city, country)
        forecastList.let { items ->
            LazyColumn {
                items(count = forecastList.size) { item ->
                    Box(
                        modifier = Modifier
                            //.width(180.dp)
                            //.height(60.dp)
                            //.padding(40.dp)
                            .background(Color(0xFF322390))

                    ) {
                        Row() {
                            //Text(forecastList[item].feelsLike?.day.toString())
                            ForecastIcon(forecastList[item].weather[0].icon)
                            ForecastTemp(forecastList[item].feelsLike?.day, forecastList[item].feelsLike?.day)
                        }
                    }
                }
                }
            }
        }
}

@Composable
fun ForecastIcon(icon: String) {

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
            }
        },
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
            }
        },
        modifier = Modifier
            .height(60.dp)
            .width(60.dp)
    )
}

@Composable
fun ForecastTemp(temp: Double?, feelsTemp: Double?) {
    Column(
        modifier = Modifier
            //.fillMaxHeight(),
    ) {
        Row {
            Text(
                text = temp.toString() + stringResource(R.string.degree),
                fontSize = 42.sp,
                color = Color.White
            )
        }
        Row {
            Text(
                text = stringResource(R.string.feelsTemp) + feelsTemp.toString() + stringResource(R.string.degree),
                color = Color.White
            )
        }
    }
}