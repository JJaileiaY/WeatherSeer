package com.example.weatherseer

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FirstScreen(
    viewModel: WeatherViewModel,
    onNavigateForecastClicked: () -> Unit,
    lat: Double,
    lon: Double,
    startLocationUpdates: () -> Unit)
{
    // Fetch Weather Data
    var weatherResult: State<NetworkResponse<WeatherMetaData>?>

    val context = LocalContext.current
    val hasLocation = remember {mutableStateOf(false)}
    val hasNotification = remember {mutableStateOf(false)}
    val showRationale = remember {mutableStateOf(false)}

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionMaps ->
        val granted = permissionMaps.values.reduce {acc, next -> acc && next}
        if (granted) {
            startLocationUpdates()
            hasLocation.value = true
            hasNotification.value = true
        } else {
            showRationale.value = true
        }
    }

    // Rationale Dialog
    if (showRationale.value) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.rationaleTitle)) },
            text = { Text(stringResource(R.string.rationaleMessage)) },
            dismissButton = {
                Button(onClick = {
                    showRationale.value = false
                }) {
                    Text(stringResource(R.string.alertCancel))
                }
            },
            confirmButton = {
                Button(onClick = {
                    showRationale.value = false
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.POST_NOTIFICATIONS)
                    )
                }) {
                    Text(stringResource(R.string.alertOk))
                }
            }
        )
    }

    // Determine if location is granted or not for which type of data to display
    if (hasLocation.value) {
        startLocationUpdates()
        viewModel.getData(lat, lon)
        weatherResult = viewModel.weatherResult.observeAsState()
        hasNotification.value = true

        if (hasNotification.value) {
            showNotification(context, weatherResult)
        }
    }
    else {
        // Check Permission In case they are granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
            == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED && zipcode == "") {

            hasLocation.value = true
            startLocationUpdates()
            viewModel.getData(lat, lon)
            weatherResult = viewModel.weatherResult.observeAsState()
            showNotification(context, weatherResult)
        }
        else {
            viewModel.getData(zipcode)
            weatherResult = viewModel.weatherResult.observeAsState()
        }
    }

    // Start of Current Screen UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF640baa), Color(0xFF8f149e))))
    ) {
        AppTitle()
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(80.dp)
                .padding(top = 10.dp)
        ) {
            TextButton(onNavigateForecastClicked, hasLocation)
            Button(
                onClick = {
                    checkPermission(context, locationPermissionLauncher, {hasLocation.value = true}) {showRationale.value = true}
                },
                colors = ButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Magenta,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .height(40.dp)
            ) {
                Image(
                    painterResource(R.drawable.location),
                    contentDescription = stringResource(R.string.locationIcon)
                )
            }
        }

        // Evaluate and Display Weather Data
        when(val result = weatherResult.value) {
            is NetworkResponse.Error -> {
                Text(text = result.message)

                // Check if there is data
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) {

                    startLocationUpdates()
                    viewModel.getData(lat, lon)
                    weatherResult = viewModel.weatherResult.observeAsState()
                }
                else {
                    viewModel.getData(zipcode)
                    weatherResult = viewModel.weatherResult.observeAsState()
                }
            }
            is NetworkResponse.Success -> {
                // City and Country
                CityState(result.data.name, result.data.sys.country)
                // Row for Temperature and Details
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .padding(horizontal = 10.dp)
                        .padding(top = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Temperature and Details
                    Temperature(result.data.main.temp, result.data.main.feelsLike)
                    TempDetails(result.data.main.tempMin,result.data.main.tempMax, result.data.main.humidity, result.data.main.pressure)
                }
                // Description and Weather Icon
                WeatherDescription(result.data.weather[0].description)
                WeatherI(result.data.weather[0].icon)
            }
            null -> {}
        }
    }
}

// Check if permissions are granted
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun checkPermission(context: Context, launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>, showLocation: () -> Unit, showRationale: () -> Unit) {
    when {
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED -> {
                    showLocation()
        }
        ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.ACCESS_COARSE_LOCATION) -> {
            showRationale()
        }
        else -> {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.POST_NOTIFICATIONS)
            )
        }
    }
}

// Call the notification
fun showNotification(context: Context, weatherResult: State<NetworkResponse<WeatherMetaData>?>) {

    when (val notifResult = weatherResult.value) {
        is NetworkResponse.Success -> {
            startNotificationService(context,
                notifResult.data.name,
                notifResult.data.sys.country,
                notifResult.data.weather[0].description,
                notifResult.data.weather[0].icon,
                notifResult.data.main.temp.toInt().toString()
            )
        }
        is NetworkResponse.Error -> {}
        null -> {}
    }
}

// To start the Notification
fun startNotificationService(
    context: Context,
    city: String,
    country: String,
    description: String,
    icon: String,
    temp: String
) {
    val serviceIntent = Intent(context, NotificationService::class.java)
    serviceIntent.putExtra(context.getString(R.string.cityName), city)
    serviceIntent.putExtra(context.getString(R.string.countryName), country)
    serviceIntent.putExtra(context.getString(R.string.desc), description)
    serviceIntent.putExtra(context.getString(R.string.icon), icon)
    serviceIntent.putExtra(context.getString(R.string.temp), temp)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ContextCompat.startForegroundService(context, serviceIntent)
    } else {
        context.startService(serviceIntent)
    }
}


////// Composable Functions for the Current Screen //////


// Create the Search Box and Button
@Composable
fun TextButton(onNavigateForecastClicked: () -> Unit, hasLocation: MutableState<Boolean>) {
    var zipEntry by remember { mutableStateOf("") }
    val context = LocalContext.current

    Spacer(modifier = Modifier.height(20.dp))

        TextField(
            zipEntry,
            onValueChange = { newZip ->
                if (newZip.length <= 5 && newZip.all {it.isDigit()} ) zipEntry = newZip
                else Toast.makeText(context, context.getString(R.string.toastLength), Toast.LENGTH_SHORT).show()
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .height(50.dp)
                .width(210.dp)
                .padding(horizontal = 20.dp)
                .testTag("textField"),
            colors = TextFieldDefaults.colors().copy(focusedContainerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        )
        Button(
            onClick = {
                if (hasLocation.value) {
                    if (zipEntry == "") {
                        zipcode = zipEntry
                        onNavigateForecastClicked() }
                    else if (!zipEntry.all {it.isDigit()} || zipEntry == "" || zipEntry.length != 5) {
                        Toast.makeText(context, context.getString(R.string.toastInvalid), Toast.LENGTH_SHORT).show() }
                    else if (zipEntry.all {it.isDigit()} && zipEntry != "" && zipEntry.length == 5) {
                        zipcode = zipEntry
                        onNavigateForecastClicked() }
                }
                else {
                if (!zipEntry.all {it.isDigit()} || zipEntry == "" || zipEntry.length != 5) {
                    Toast.makeText(context, context.getString(R.string.toastInvalid), Toast.LENGTH_SHORT).show() }
                else if (zipEntry.all {it.isDigit()} && zipEntry != "" && zipEntry.length == 5) {
                    zipcode = zipEntry
                    onNavigateForecastClicked() }
                } },
            colors = ButtonColors(
                contentColor = Color.White,
                containerColor = Color.Magenta,
                disabledContentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        ) {
            Text(stringResource(R.string.search))
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
            fontSize = 24.sp,
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
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x60322390))
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(15.dp)
                .padding(horizontal = 20.dp)
        ) {
            Row {
                Text(
                    text = temp.toInt().toString() + stringResource(R.string.degree),
                    fontSize = 62.sp,
                    color = Color.White
                )
            }
            Row {
                Text(
                    text = stringResource(R.string.feelsTemp) + feelsTemp.toInt().toString() + stringResource(R.string.degree),
                    color = Color.White
                )
            }
        }
    }
}

// Create the Details of the Temperature
@Composable
fun TempDetails(low: Double, high: Double, humidity: Int, pressure: Int) {

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x60322390))
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                text = stringResource(R.string.low) + low.toInt().toString() + stringResource(R.string.degree),
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = stringResource(R.string.high) + high.toInt().toString() + stringResource(R.string.degree),
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = stringResource(R.string.humidity) + humidity.toString() + stringResource(R.string.percent),
                fontSize = 16.sp,
                color = Color.White
            )
            Text(
                text = stringResource(R.string.pressure) + pressure.toString() + stringResource(R.string.hPa),
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun WeatherDescription(desc: String) {
    Column (
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(desc.capitalizeFirstLetter(),
            fontSize = 24.sp,
            color = Color.White,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 50.dp)
        )
    }
}

@Composable
fun WeatherI(icon: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
    ) {
        Column(modifier = Modifier
            .fillMaxSize()
            .align(Alignment.Bottom))
        {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center )
            {
                Image(
                    painterResource(R.drawable.crystalb),
                    contentDescription = stringResource(R.string.crystalBall),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 50.dp)
                )
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
                        .height(120.dp)
                        .width(120.dp)
                )
            }
        }
    }
}
