package com.example.pogodynka.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pogodynka.R
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class WeatherApiResponse(
    val main: Main,
    val sys: Sys,
    var coord: Coord,
    val wind: Wind,
    val clouds: Clouds,
    var name: String,
    val timezone: Int,
    var favorite : Boolean = false,
    val weather: List<Weather>,
) : Parcelable{

    @Parcelize
    @Entity(tableName = "favorite_coord")
    data class Coord(
        val lat: Double,
        val lon: Double,
        @PrimaryKey()
        var name : String = ""
    ) : Parcelable

    @Parcelize
    data class Main(
        val feels_like: Double,
        val humidity: Int,
        val pressure: Int,
        val temp: Double,
        val temp_max: Double,
        val temp_min: Double
    ) : Parcelable

    @Parcelize
    data class Wind(
        val speed: Double
    ) : Parcelable

    @Parcelize
    data class Clouds(
        val all: Int
    ) : Parcelable

    @Parcelize
    data class Sys(
        val sunrise: Int,
        val sunset: Int,
    ) : Parcelable

    @Parcelize
    data class Weather(
        val description: String,
        val icon: String,
        val main: String
    ) : Parcelable

}
