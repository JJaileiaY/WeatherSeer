package com.example.weatherseer

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FirstScreen(viewModel: WeatherViewModel, onNavigateForecastClicked: () -> Unit) {

    // Fetch Weather Data
    viewModel.GetQueryInfo()
    viewModel.getData(stringResource(R.string.city))
    val weatherResult = viewModel.weatherResult.observeAsState()

    ////////////// Get textbox to only be 5 numbers and numpad, send popup? error if bad zipcode
    ////////// Ask if still hard-code city?, fix design especially for text and button

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {
        AppTitle()             // App Title
        TextButton(onNavigateForecastClicked)    // Zipcode Entry and Button

        // Evaluate Weather Data
        when(val result = weatherResult.value) {
            is NetworkResponse.Error -> {
                Text(text = result.message)
            }
            is NetworkResponse.Success -> {
                CityState(result.data.name, result.data.sys.country)        // City and Country

                // Row for Temp and Image
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Temperature(result.data.main.temp, result.data.main.feelsLike)   // Temperature
                    SunnyImg()          // Sun Image
                }
                // Temp Details
                TempDetails(result.data.main.tempMin,result.data.main.tempMax, result.data.main.humidity, result.data.main.pressure)
            }
            null -> {}
        }
    }
}

@Composable
fun TextButton(onNavigateForecastClicked: () -> Unit) {
    var zipEntry by remember { mutableStateOf("") }
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            zipEntry,
            onValueChange = {
                zipEntry = it
            },
            modifier = Modifier
                .height(56.dp),
            colors = TextFieldDefaults.colors().copy(focusedContainerColor = Color.White)
        )
        Button(
            onClick = { zipcode = zipEntry; onNavigateForecastClicked() },
            colors = ButtonColors(
                contentColor = Color.Black,
                containerColor = Color.Transparent,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        ) {}
    }
}


// Create the App Title
@Composable
fun AppTitle() {
    Row {
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier
                .background(Color.LightGray)
                .fillMaxWidth()
                .padding(18.dp),
            fontSize = 20.sp,
        )
    }
}

// Create the City and Country
@Composable
fun CityState(city: String, country: String) {
    Row {
        Text(
            text = city + stringResource(R.string.comma) + country,
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            textAlign = TextAlign.Center,
            fontSize = 20.sp,
        )
    }
}

// Create the Temperature
@Composable
fun Temperature(temp: Double, feelsTemp: Double) {
    Column(
        modifier = Modifier
            .fillMaxHeight(),
    ) {
        Row {
            Text(
                text = temp.toString() + stringResource(R.string.degree),
                fontSize = 62.sp
            )
        }
        Row {
            Text(
                text = stringResource(R.string.feelsTemp) + feelsTemp.toString() + stringResource(R.string.degree)
            )
        }
    }
}

// Create the Image
@Composable
fun SunnyImg() {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(100.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Image(
            painterResource(R.drawable.sunny),
            contentDescription = stringResource(R.string.sunny),
            modifier = Modifier
                .height(80.dp)
                .width(80.dp)
                .align(Alignment.CenterHorizontally)
        )
    }
}

// Create the Details of the Temperature
@Composable
fun TempDetails(low: Double, high: Double, humidity: Int, pressure: Int) {
    Row {
        Column(
            modifier = Modifier.padding(30.dp)
        ) {
            Text(
                text = stringResource(R.string.low) + low.toString() + stringResource(R.string.degree),
                fontSize = 18.sp
            )
            Text(
                text = stringResource(R.string.high) + high.toString() + stringResource(R.string.degree),
                fontSize = 18.sp
            )
            Text(
                text = stringResource(R.string.humidity) + humidity.toString() + stringResource(R.string.percent),
                fontSize = 18.sp
            )
            Text(
                text = stringResource(R.string.pressure) + pressure.toString() + stringResource(R.string.hPa),
                fontSize = 18.sp
            )
        }
    }
}