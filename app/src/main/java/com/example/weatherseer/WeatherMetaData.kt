package com.example.weatherseer

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Class for a Weather Object
@Serializable
data class WeatherMetaData(
    var coord: Coord,
    var weather: List<Weather>,
    var base: String,
    var main: Main,
    var visibility: Int,
    var wind: Wind? = null,
    var rain: Rain? = null,
    var clouds: Clouds? = null,
    var dt: Int,
    var sys: Sys,
    var timezone: Int,
    var id: Int,
    var name: String,
    var cod: Int
)

@Serializable
data class Coord(
    var lon: Double,
    var lat: Double
)

@Serializable
data class Weather(
    var id: Int,
    var main: String,
    var description: String,
    var icon: String
)

@Serializable
data class Main(
    var temp: Double,
    @SerialName("feels_like")
    var feelsLike: Double,
    @SerialName("temp_min")
    var tempMin: Double,
    @SerialName("temp_max")
    var tempMax: Double,
    var humidity: Int,
    var pressure: Int,
    @SerialName("sea_level")
    var seaLevel: Int? = null,
    @SerialName("grnd_level")
    var grndLevel: Int? = null
)

@Serializable
data class Wind(
    var speed: Double? = null,
    var deg: Int? = null,
    var gust: Double? = null
)

@Serializable
data class Rain(
    @SerialName("1h")
    var oneh: Double? = null
)

@Serializable
data class Clouds(
    var all: Int? = null
)

@Serializable
data class Sys(
    var type: Int,
    var id: Int,
    var country: String,
    var sunrise: Int,
    var sunset: Int
)
