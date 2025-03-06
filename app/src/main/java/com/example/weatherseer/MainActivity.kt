package com.example.weatherseer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.weatherseer.ui.theme.WeatherSeerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        setContent {
            WeatherSeerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FirstScreen(weatherViewModel)
                }
            }
        }
    }
}

@Composable
fun FirstScreen(viewModel: WeatherViewModel) {

    // Fetch Weather Data
    viewModel.GetQueryInfo()
    viewModel.getData(stringResource(R.string.city))
    val weatherResult = viewModel.weatherResult.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {
        AppTitle()      // App Title

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
