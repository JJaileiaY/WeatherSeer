package com.example.weatherseer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FirstScreen(viewModel: WeatherViewModel, onNavigateForecastClicked: () -> Unit) {

    // Fetch Weather Data
    viewModel.GetQueryInfo()
    //viewModel.getData(zipcode)
    val weatherResult: State<NetworkResponse<WeatherMetaData>?>

    val context = LocalContext.current
    val hasLocation = remember {mutableStateOf(false)}
    val hasNotification = remember {mutableStateOf(false)}
    val showRationale = remember {mutableStateOf(false)}


    // how to use location service to update location etc.
    ///// how to make it display current location vs zipcode location data,
    // how to get rationale to show properly
    ///// Add notification permissions and make notification persist and open app when clicked
    /// When hasLocation true, doesn't start up with location or go back to current screen with
    // location, only updates when click button.

// might not be working
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                hasLocation.value = true
            }
            permissions.getOrDefault(Manifest.permission.POST_NOTIFICATIONS, false) -> {
                hasNotification.value = true
            }
            else -> {
                showRationale.value = true
            }
        }
    }

    if (showRationale.value) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(R.string.rationaleTitle)) },
            text = { Text(stringResource(R.string.rationaleMessage)) },
            confirmButton = {
                Button(onClick = {
                    showRationale.value = false
                }) {
                    Text(stringResource(R.string.alertOk))
                }
            },
            dismissButton = null
        )
    }

    if (hasLocation.value) {
        LocationGranted(viewModel)
        weatherResult = viewModel.weatherResultLL.observeAsState()
    }
    else {
        viewModel.getData(zipcode)
        weatherResult = viewModel.weatherResult.observeAsState()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF640baa), Color(0xFF8f149e))))

    ) {
        // App Title, Search Bar and Button
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
                modifier = Modifier.padding(horizontal = 15.dp).height(40.dp)
            ) {
                Image(
                    painterResource(R.drawable.location),
                    contentDescription = "Location"
                )
            }
        }

        // Evaluate Weather Data
        when(val result = weatherResult.value) {
            is NetworkResponse.Error -> {
                Text(text = result.message)
            }
            is NetworkResponse.Success -> {
                CityState(result.data.name, result.data.sys.country)        // City and Country

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
                    Temperature(result.data.main.temp, result.data.main.feelsLike)   // Temperature
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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun checkPermission(context: Context, launcher: ManagedActivityResultLauncher<Array<String>, Map<String, @JvmSuppressWildcards Boolean>>, showLocation: () -> Unit, showRationale: () -> Unit) {
    when {
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED -> {
                    showLocation()
        }
        else -> {
            launcher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.POST_NOTIFICATIONS)
            )
        }
    }
    // Show the Rationale
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        == PackageManager.PERMISSION_DENIED) {
        showRationale()
    }
}

@SuppressLint("MissingPermission")
@Composable
fun LocationGranted(viewModel: WeatherViewModel) {

    Log.i("In LocationGranted", "Succeeded")

    val context = LocalContext.current

    val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    ///// Only getting Last Location, need current location
    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
        location?.let {
            viewModel.getDataLL(it.latitude, it.longitude)
        }
    }

    /////////// Add location updates for notifications

}






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
                .padding(horizontal = 20.dp),
            colors = TextFieldDefaults.colors().copy(focusedContainerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        )
        Button(
            onClick = {

                if (hasLocation.value) {
                    if (zipEntry == "") {
                        //zipcode = zipEntry
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