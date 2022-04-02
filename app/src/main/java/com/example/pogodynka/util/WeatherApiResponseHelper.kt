package com.example.pogodynka.util


import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.example.pogodynka.R
import java.time.Instant
import java.util.*

object WeatherApiResponseHelper {

//    fun getLocationName(place: Place): String {
//        var city: String = ""
//        var country: String = ""
//        for (i in place.addressComponents.asList()) {
//            var addressObj = i
//            for (j in addressObj.types) {
//                if (j == "locality") {
//                    city = addressObj.name
//                }
//                if (j == "country") {
//                    country = addressObj.name
//                }
//            }
//        }
//        return "${city}, $country"
//    }

    fun getLocationName(lat : Double, lon : Double, context : Context): String {
        val geoCoder = Geocoder(context)
        val locations = geoCoder.getFromLocation(lat, lon, 10)
            for(loc in locations){
                if(loc.locality != null && loc.locality.isNotEmpty()){

                    Log.i("LOL", " ${loc.latitude},${loc.longitude}")
                    Log.i("LOL", " ${loc.locality},${loc.countryName}")
                    return "${loc.locality}, ${loc.countryName}"
                }
            }
        return return "${locations.first().locality}, ${locations.first().countryName}"
    }

    fun convertToTimeZone(timezone: Int): TimeZone {
        return if (timezone == 0) {
            TimeZone.getTimeZone("GMT+00")
        } else {
            val hours = (timezone / 60) / 60
            if (hours > 0) {
                TimeZone.getTimeZone("GMT+${hours}")
            } else {
                TimeZone.getTimeZone("GMT${hours}")
            }
        }
    }

    fun getWeatherIcon(icon: String): Int {
        when (icon) {
            "01d" -> return R.drawable.clear_d
            "01n" -> return R.drawable.clear_n
            "02d" -> return R.drawable.few_clouds_d
            "02n" -> return R.drawable.few_clouds_n
            "03d" -> return R.drawable.clouds
            "03n" -> return R.drawable.clouds
            "04d" -> return R.drawable.broken_clouds
            "04n" -> return R.drawable.broken_clouds
            "09d" -> return R.drawable.shower_rain
            "09n" -> return R.drawable.shower_rain
            "10d" -> return R.drawable.rain_d
            "10n" -> return R.drawable.rain_n
            "11d" -> return R.drawable.thunderstorm
            "11n" -> return R.drawable.thunderstorm
            "13d" -> return R.drawable.snow
            "13n" -> return R.drawable.snow
            "50d" -> return R.drawable.mist
            else -> return R.drawable.mist
        }
    }

    fun timeStampToDateTime(timestamp: Int, timezone: Int): String {
        val hour = Instant.ofEpochSecond(timestamp.toLong())
            .atZone(convertToTimeZone(timezone).toZoneId()).hour
        val minute = Instant.ofEpochSecond(timestamp.toLong())
            .atZone(convertToTimeZone(timezone).toZoneId()).minute
        return "${hour}:${minute}"
    }

}