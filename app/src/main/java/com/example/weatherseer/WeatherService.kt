package com.example.weatherseer

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// Interface to fetch weather data.
interface WeatherService {

    @GET("/data/2.5/weather")
    suspend fun getWeather(
        @Query("zip") zip: String,
        @Query("appid") appid: String,
        @Query("units") units: String
    ): Response<WeatherMetaData>

    @GET("/data/2.5/forecast/daily")
    suspend fun getForecast(
        @Query("zip") zip: String,
        @Query("appid") appid: String,
        @Query("cnt") days: Int,
        @Query("units") units: String
    ): Response<ForecastMetaData>

}