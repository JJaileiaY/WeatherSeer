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

    // Variables to hold query info.
    private var apiKey = ""
    private var units = ""
    private var errMessage = ""

    @Composable
    fun GetQueryInfo() {
        apiKey = stringResource(R.string.apiKey)
        units = stringResource(R.string.units)
        errMessage = stringResource(R.string.errMessage)
    }

    // Get the weather data, determine if success or not.
    fun getData(city: String){

        viewModelScope.launch {
            try {
                val response = weatherService.getWeather(city, apiKey, units)
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
}

