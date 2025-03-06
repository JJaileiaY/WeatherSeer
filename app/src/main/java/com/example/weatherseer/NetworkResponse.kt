package com.example.weatherseer

// For determining when response succeeds or fails. T is WeatherMetaData.
sealed class NetworkResponse<out T> {
    data class Success<out T>(val data: T): NetworkResponse<T>()
    data class Error(val message: String) : NetworkResponse<Nothing>()
}