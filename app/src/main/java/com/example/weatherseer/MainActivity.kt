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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherseer.ui.theme.WeatherSeerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherSeerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FirstScreen()
                }
            }
        }
    }
}

@Composable
fun FirstScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {
        AppTitle()      // App Title
        CityState()     // City and State

        // Row for Temp and Image
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
                horizontalArrangement = Arrangement.SpaceAround
        ) {
            Temperature()   // Temperature
            SunnyImg()      // Sun Image
        }
        TempDetails()      // Details
    }
}

// Create the App Title
@Composable
fun AppTitle() {
    Row() {
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

// Create the City State
@Composable
fun CityState() {
    Row() {
        Text(
            text = stringResource(R.string.cityState),
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
fun Temperature() {
    Column(
        modifier = Modifier
            .fillMaxHeight(),
    ) {
        Row() {
            Text(
                text = stringResource(R.string.temp),
                fontSize = 72.sp
            )
        }
        Row() {
            Text(
                text = stringResource(R.string.feelsTemp)
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
fun TempDetails() {
    Row() {
        Column(
            modifier = Modifier.padding(30.dp)
        ) {
            Text(
                text = stringResource(R.string.low),
                fontSize = 18.sp
            )
            Text(
                text = stringResource(R.string.high),
                fontSize = 18.sp
            )
            Text(
                text = stringResource(R.string.humidity),
                fontSize = 18.sp
            )
            Text(
                text = stringResource(R.string.pressure),
                fontSize = 18.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FirstScreenPreview() {
    WeatherSeerTheme {
        FirstScreen()
    }
}