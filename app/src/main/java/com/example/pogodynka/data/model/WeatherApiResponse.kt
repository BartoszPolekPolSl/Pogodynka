package com.example.pogodynka.data.model

import com.example.pogodynka.R
import java.util.*

data class WeatherApiResponse(
    val main: Main,
    val sys: Sys,
    val timezone: Int,
    val weather: List<Weather>,
) {

    data class Main(
        val feels_like: Double,
        val humidity: Int,
        val pressure: Int,
        val temp: Double,
        val temp_max: Double,
        val temp_min: Double
    )

    data class Sys(
        val sunrise: Int,
        val sunset: Int,
    )

    data class Weather(
        val description: String,
        val icon: String,
        val main: String
    )

}
