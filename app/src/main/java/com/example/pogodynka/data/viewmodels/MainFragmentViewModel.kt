package com.example.pogodynka.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pogodynka.data.model.WeatherApiResponse
import com.example.pogodynka.network.WeatherApi
import com.example.pogodynka.network.WeatherApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.Response

class MainFragmentViewModel : ViewModel() {
    var weatherResponse : MutableLiveData<Response<WeatherApiResponse>> = MutableLiveData()
    fun getWeather(){
        viewModelScope.launch(Dispatchers.IO) {
            weatherResponse.postValue(WeatherApi.api.getWeather(1.0,1.0))
        }
    }
}