package com.example.pogodynka.data.model

data class WeatherApiResponse(
    val base: String,
    val coord: Coord,
    val main: Main,
    val weather: List<Weather>
) {
    data class Coord(
        val lat: Double,
        val lon: Double
    )

    data class Main(
        val feels_like: Double,
        val humidity: Int,
        val pressure: Int,
        val temp: Double,
        val temp_max: Double,
        val temp_min: Double
    )

    data class Weather(
        val description: String,
        val icon: String,
        val id: Int,
        val main: String
    )
}