package com.example.weatherseer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val weatherService: WeatherService,
    private val appid: String,
    private val units: String,
    private val errMessage: String
    ): ViewModel() {

    // LiveData variables
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherMetaData>>()
    val weatherResult: LiveData<NetworkResponse<WeatherMetaData>> = _weatherResult
    private val _forecastResult = MutableLiveData<NetworkResponse<ForecastMetaData>>()
    val forecastResult: LiveData<NetworkResponse<ForecastMetaData>> = _forecastResult

    // Get the weather data, determine if success or not.
    fun getData(zip: String) {

        viewModelScope.launch {
            try {
                val response = weatherService.getWeather(zip, appid, units)
                if (response.isSuccessful) {
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

    // Get the weather data using Lat and Lon, determine if success or not.
    fun getData(lat: Double, lon: Double) {

        viewModelScope.launch {
            try {
                val response = weatherService.getWeatherLL(lat, lon, appid, units)
                if (response.isSuccessful) {
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
                if (response.isSuccessful) {
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

    // Get the forecast data using Lat and Lon, determine if success or not.
    fun getForecastData(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = weatherService.getForecastLL(lat, lon, appid, days = 16, units)
                if (response.isSuccessful) {
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

}



