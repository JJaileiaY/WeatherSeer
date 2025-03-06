package com.example.weatherseer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {

    private val weatherService = RetrofitInstance.weatherService
    private val _weatherResult = MutableLiveData<NetworkResponse<WeatherMetaData>>()
    val weatherResult: LiveData<NetworkResponse<WeatherMetaData>> = _weatherResult

    fun getData(city: String){

        viewModelScope.launch {
            try {
                Log.i("In Try Block:", "Before Response")
                val response = weatherService.getWeather("Chicago", "21c5d48b8f5a5f7d5a438d98819ab5a0", "imperial")
                Log.i("Passed Response:", "Before If")
                if(response.isSuccessful) {
                    response.body()?.let {
                        _weatherResult.value = NetworkResponse.Success(it)
                        Log.i("Response:", _weatherResult.toString())
                    }
                } else {
                    _weatherResult.value = NetworkResponse.Error("Couldn't find the data you are looking for.")
                    Log.i("Not Success:", "In the Else")
                }
            } catch (e: Exception) {
                _weatherResult.value = NetworkResponse.Error("Couldn't find the data you are looking for.")
                Log.i("Exception Thrown:", "In the Catch")
            }
        }
    }
}

