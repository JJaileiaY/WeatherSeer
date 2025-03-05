package com.example.weatherseer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel: ViewModel() {

    private val weatherService = RetrofitInstance.weatherService

    fun getData(city: String){

        viewModelScope.launch {
            val response = weatherService.getWeather("Chicago", "21c5d48b8f5a5f7d5a438d98819ab5a0")
            if(response.isSuccessful) {
                Log.i("Response:", response.message())
            } else {
                Log.i("Error:", response.message())
            }
        }
    }

}

