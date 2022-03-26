package com.example.pogodynka.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pogodynka.data.model.WeatherApiResponse
import com.example.pogodynka.network.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel() : ViewModel() {
    var weatherResponse : MutableLiveData<Response<WeatherApiResponse>> = MutableLiveData()
    var weatherFavorites : MutableList<WeatherApiResponse> = mutableListOf()

    fun getWeather(lan : Double, lon : Double){
        viewModelScope.launch(Dispatchers.IO) {
            weatherResponse.postValue(WeatherApi.api.getWeather(lan,lon))
        }
    }

    fun addOrRemoveWeatherFavorites(weather : WeatherApiResponse): Boolean {
        if(weatherFavorites.contains(weather)){
            weatherFavorites.remove(weather)
            return false
        }
        else{
            weatherFavorites.add(weather)
            return true
        }
    }
}
