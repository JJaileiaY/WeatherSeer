package com.example.weatherseer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
            //.background(Color.Green)
            .fillMaxSize()

    ) {
        Row() {
            Text(
                text = stringResource(R.string.app_name),
                modifier = Modifier
                    .background(Color.LightGray)
                    .fillMaxWidth()
                    .padding(18.dp),
                //textAlign = TextAlign.Center,
                fontSize = 20.sp,
            )
        }
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



}


@Preview(showBackground = true)
@Composable
fun FirstScreenPreview() {
    WeatherSeerTheme {
        FirstScreen()
    }
}