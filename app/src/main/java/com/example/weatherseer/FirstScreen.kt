package com.example.weatherseer

import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FirstScreen(viewModel: WeatherViewModel, onNavigateForecastClicked: () -> Unit) {

    // Fetch Weather Data
    viewModel.GetQueryInfo()
    viewModel.getData(zipcode)
    val weatherResult = viewModel.weatherResult.observeAsState()

    ////////// fix design especially for text and button

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF640baa), Color(0xFF8f149e))))

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
                    WeatherIcon(result.data.weather[0].icon)          // Icon
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
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(20.dp))

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .height(50.dp)
    ) {
        TextField(
            zipEntry,
            onValueChange = { newZip ->
                if (newZip.length <= 5 && newZip.all {it.isDigit()} ) zipEntry = newZip
                else Toast.makeText(context, "Zipcode cannot be more than 5 digits", Toast.LENGTH_SHORT).show()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .height(50.dp)
                .padding(horizontal = 15.dp),
            colors = TextFieldDefaults.colors().copy(focusedContainerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        )
        Button(
            onClick = {
                if (!zipEntry.all {it.isDigit()} || zipEntry == "" || zipEntry.length != 5) {
                    Toast.makeText(context, "Invalid Zipcode", Toast.LENGTH_SHORT).show() }
                else if (zipEntry.all {it.isDigit()} && zipEntry != "" && zipEntry.length == 5) {
                    zipcode = zipEntry;
                    onNavigateForecastClicked() } },
            colors = ButtonColors(
                contentColor = Color.White,
                containerColor = Color.Magenta,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        ) {
            Text("Search")
        }
    }
}


// Create the App Title
@Composable
fun AppTitle() {
    Row {
        Text(
            text = stringResource(R.string.app_name),
            modifier = Modifier
                .background(Color(0xFFa121ca))
                .fillMaxWidth()
                .padding(18.dp),
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center
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
            color = Color.White
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
                fontSize = 62.sp,
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

// Create the Image
@Composable
fun WeatherIcon(icon: String) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(100.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

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
                else -> {painterResource(R.drawable.sunny)}
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
                else -> {stringResource(R.string.sunny)}
            },
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
                fontSize = 18.sp,
                color = Color.White
            )
            Text(
                text = stringResource(R.string.high) + high.toString() + stringResource(R.string.degree),
                fontSize = 18.sp,
                color = Color.White
            )
            Text(
                text = stringResource(R.string.humidity) + humidity.toString() + stringResource(R.string.percent),
                fontSize = 18.sp,
                color = Color.White
            )
            Text(
                text = stringResource(R.string.pressure) + pressure.toString() + stringResource(R.string.hPa),
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }
}