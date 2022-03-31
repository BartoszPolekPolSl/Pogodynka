package com.example.pogodynka.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pogodynka.R
import java.util.*

data class WeatherApiResponse(
    val main: Main,
    val sys: Sys,
    var coord: Coord,
    var name: String,
    val timezone: Int,
    var favorite : Boolean = false,
    val weather: List<Weather>,
) {

    @Entity(tableName = "favorite_coord")
    data class Coord(
        val lat: Double,
        val lon: Double,
        @PrimaryKey()
        var name : String = ""
    )

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
