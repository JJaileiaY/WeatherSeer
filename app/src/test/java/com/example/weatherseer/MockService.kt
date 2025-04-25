package com.example.weatherseer

import retrofit2.Response

class MockService: WeatherService {

    var mockWeatherResponse: Response<WeatherMetaData>? = null
    var mockForecastResponse: Response<ForecastMetaData>? = null

    override suspend fun getWeather(zip: String, appid: String, units: String): Response<WeatherMetaData> {
        return mockWeatherResponse?:
        throw IllegalStateException("mockWeatherResponse null")
    }

    override suspend fun getForecast(
        zip: String,
        appid: String,
        days: Int,
        units: String
    ): Response<ForecastMetaData> {
        return mockForecastResponse?:
        throw IllegalStateException("mockForecastResponse null")
    }

    override suspend fun getWeatherLL(
        lat: Double,
        lon: Double,
        appid: String,
        units: String
    ): Response<WeatherMetaData> {
        return mockWeatherResponse?:
        throw IllegalStateException("mockWeatherResponse null")
    }

    override suspend fun getForecastLL(
        lat: Double,
        lon: Double,
        appid: String,
        days: Int,
        units: String
    ): Response<ForecastMetaData> {
        return mockForecastResponse?:
        throw IllegalStateException("mockForecastResponse null")
    }
}