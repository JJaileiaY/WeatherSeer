package com.example.weatherseer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Class for Forecast Object
@Serializable
data class ForecastMetaData(
    var city: City,
    var cod: String? = null,
    var message: Double? = null,
    var cnt: Int? = null,
    var list: List<ForecastList> = arrayListOf()
)

@Serializable
data class City(
    var id: Int,
    var name: String,
    var coord: Coord,
    var country: String,
    var population: Int,
    var timezone: Int
)

@Serializable
data class Temp (
    var day: Double? = null,
    var min: Double? = null,
    var max: Double? = null,
    var night: Double? = null,
    var eve: Double? = null,
    var morn: Double? = null
)

@Serializable
data class FeelsLike (
    var day: Double? = null,
    var night: Double? = null,
    var eve: Double? = null,
    var morn: Double? = null
)

@Serializable
data class ForecastList (
    var dt: Int? = null,
    var sunrise: Int? = null,
    var sunset: Int? = null,
    var temp: Temp? = null,
    @SerialName("feels_like")
    var feelsLike: FeelsLike? = null,
    var pressure: Int? = null,
    var humidity: Int? = null,
    var weather: ArrayList<Weather> = arrayListOf(),
    var speed: Double? = null,
    var deg: Int? = null,
    var gust: Double? = null,
    var pop: Double? = null,
    var rain: Double? = null
)