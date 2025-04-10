package com.example.weatherseer


/**
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
 **/

/**
val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

val locationCallback = object : LocationCallback() {
override fun onLocationResult(locationResult: LocationResult) {
Log.d("LoCallBack", "Succeeded Call")
locationResult.let {
Log.d("LoCallBack2", "Succeeded Not Null")
for (location in locationResult.locations) {
Log.d("LoCallBack3", "Succeeded Has LatLon")
viewModel.getDataLL(location.latitude, location.longitude)
}
}
}
}
 **/

/**
@SuppressLint("MissingPermission")
@Composable
fun LocationGranted(viewModel: WeatherViewModel) {

Log.i("In LocationGranted", "Succeeded")

val context = LocalContext.current
val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

/**
val locationCallback = object : LocationCallback() {
override fun onLocationResult(locationResult: LocationResult) {
locationResult.lastLocation?.let { location ->
viewModel.getDataLL(location.latitude, location.longitude)
}
}
}
**/
/
val locationCallback = object : LocationCallback() {
override fun onLocationResult(locationResult: LocationResult) {
Log.d("LoCallBack", "Succeeded Call")
locationResult.let {
Log.d("LoCallBack2", "Succeeded Not Null")
for (location in locationResult.locations) {
Log.d("LoCallBack3", "Succeeded Has LatLon")
viewModel.getDataLL(location.latitude, location.longitude)
}
}
}
}

fun startLocationUpdates() {

if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION
) != PackageManager.PERMISSION_GRANTED
&& ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS
) != PackageManager.PERMISSION_GRANTED
) {
Log.d("StartLocationUpdates", "Failed")
return
}
else {
Log.d("StartLocationUpdates", "Succeeded")
fusedLocationClient.requestLocationUpdates(
createLocationRequest(),
locationCallback,
Looper.getMainLooper()
)
}
}

fun stopLocationUpdates() {
fusedLocationClient.removeLocationUpdates(locationCallback)
}
//startLocationUpdates()

/**
///// Only getting Last Location, need current location
fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
location?.let {
viewModel.getDataLL(it.latitude, it.longitude)
}
}
**/
//stopLocationUpdates()
}
 **/

/**
fun createLocationRequest(): LocationRequest {
Log.d("LoRequest", "Succeeded")
return LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, TimeUnit.MINUTES.toMillis(15))
.setMinUpdateIntervalMillis(TimeUnit.MINUTES.toMillis(5))
.setMaxUpdateDelayMillis(TimeUnit.MINUTES.toMillis(30))
.build()
}

@SuppressLint("MissingPermission")
private fun startLocationUpdates(context: Context, fusedLocationClient: FusedLocationProviderClient, locationCallback: LocationCallback) {
if (ContextCompat.checkSelfPermission(
context,
Manifest.permission.ACCESS_COARSE_LOCATION
) == PackageManager.PERMISSION_GRANTED
) {
Log.d("StartLocationUpdates", "Succeeded")
fusedLocationClient.requestLocationUpdates(
createLocationRequest(),
locationCallback,
Looper.getMainLooper()
)
} else {
Log.d("StartLocationUpdates", "Failed - Permissions not granted")
}
}

private fun stopLocationUpdates(fusedLocationClient: FusedLocationProviderClient, locationCallback: LocationCallback) {
fusedLocationClient.removeLocationUpdates(locationCallback)
}

 **/