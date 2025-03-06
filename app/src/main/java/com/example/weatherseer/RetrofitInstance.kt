package com.example.weatherseer

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

// Retrofit object to fetch and parse JSON/Weather data.
object RetrofitInstance {

    private const val BASEURL = "https://api.openweathermap.org/data/2.5/"

    private fun getInstance(): Retrofit {

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroJson = Json {ignoreUnknownKeys = true}

        return Retrofit.Builder()
            .baseUrl(BASEURL)
            .client(client)
            .addConverterFactory(
                retroJson.asConverterFactory(
                "application/json".toMediaType()))
            .build()
    }
    val weatherService : WeatherService = getInstance().create(WeatherService::class.java)
}