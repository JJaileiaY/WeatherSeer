package com.example.weatherseer

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {

    // LiveData variables
    private val weatherService = RetrofitInstance.weatherService
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherMetaData>>()
    val weatherResult: LiveData<NetworkResponse<WeatherMetaData>> = _weatherResult

    private val _forecastResult = MutableLiveData<NetworkResponse<ForecastMetaData>>()
    val forecastResult: LiveData<NetworkResponse<ForecastMetaData>> = _forecastResult

    // LiveData variables for Lat and Lon
    private val _weatherResultLL = MutableLiveData<NetworkResponse<WeatherMetaData>>()
    val weatherResultLL: LiveData<NetworkResponse<WeatherMetaData>> = _weatherResultLL

    private val _forecastResultLL = MutableLiveData<NetworkResponse<ForecastMetaData>>()
    val forecastResultLL: LiveData<NetworkResponse<ForecastMetaData>> = _forecastResultLL


    // Variables to hold query info.
    private var appid = ""
    private var units = ""
    private var errMessage = ""

    @Composable
    fun GetQueryInfo() {
        appid = stringResource(R.string.appid)
        units = stringResource(R.string.units)
        errMessage = stringResource(R.string.errMessage)
    }

    // Get the weather data, determine if success or not.
    fun getData(zip: String) {

        viewModelScope.launch {
            try {
                val response = weatherService.getWeather(zip, appid, units)
                if(response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error(errMessage)
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error(errMessage)
            }
        }
    }

    fun getData(lat: Double, lon: Double) {

        viewModelScope.launch {
            try {
                val response = weatherService.getWeatherLL(lat, lon, appid, units)
                if(response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error(errMessage)
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error(errMessage)
            }
        }
    }

    // Get the forecast data, determine if success or not.
    fun getForecastData(zip: String) {
        viewModelScope.launch {
            try {
                val response = weatherService.getForecast(zip, appid, days = 16, units)
                if(response.isSuccessful) {
                    response.body()?.let {
                        _forecastResult.value = NetworkResponse.Success(it)
                    }
                } else {
                    _forecastResult.value = NetworkResponse.Error(errMessage)
                }
            } catch (e: Exception) {
                _forecastResult.value = NetworkResponse.Error(errMessage)
            }
        }
    }


/**
    // Get the weather data using Lat and Lon, determine if success or not.
    fun getDataLL(lat: Double, lon: Double) {

        // set to weatherresult instead which then only need to use v.weatherResult.observe...
        viewModelScope.launch {
            try {
                val response = weatherService.getWeatherLL(lat, lon, appid, units)
                if(response.isSuccessful) {
                    response.body()?.let {
                        _weatherResultLL.value = NetworkResponse.Success(it)
                    }
                } else {
                    _weatherResultLL.value = NetworkResponse.Error(errMessage)
                }
            } catch (e: Exception) {
                _weatherResultLL.value = NetworkResponse.Error(errMessage)
            }
        }
    }
**/
    // Get the forecast data using Lat and Lon, determine if success or not.
    fun getForecastDataLL(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = weatherService.getForecastLL(lat, lon, appid, days = 16, units)
                if(response.isSuccessful) {
                    response.body()?.let {
                        _forecastResultLL.value = NetworkResponse.Success(it)
                    }
                } else {
                    _forecastResultLL.value = NetworkResponse.Error(errMessage)
                }
            } catch (e: Exception) {
                _forecastResultLL.value = NetworkResponse.Error(errMessage)
            }
        }
    }
}

