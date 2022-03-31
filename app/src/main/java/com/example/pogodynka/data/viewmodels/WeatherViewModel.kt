package com.example.pogodynka.data.viewmodels

import android.util.Log
import androidx.lifecycle.*
import com.example.pogodynka.data.database.PogodynkaDao
import com.example.pogodynka.data.model.WeatherApiResponse
import com.example.pogodynka.network.WeatherApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(private val pogodynkaDao: PogodynkaDao) : ViewModel() {

    private val _weatherResponse : MutableLiveData<Response<WeatherApiResponse>> = MutableLiveData()
    val weatherResponse : LiveData<Response<WeatherApiResponse>>
        get() = _weatherResponse

    private val _favoriteWeatherList : MutableLiveData<MutableList<WeatherApiResponse>> = MutableLiveData(
        mutableListOf())
    val favoriteWeatherList : LiveData<MutableList<WeatherApiResponse>>
        get() = _favoriteWeatherList

    init {
        loadFavoriteWeather()
    }

    fun getWeather(lat : Double, lon : Double){
        viewModelScope.launch(Dispatchers.IO) {
            _weatherResponse.postValue(WeatherApi.api.getWeather(lat,lon))
        }
    }

    private fun loadFavoriteWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteCoordsList = viewModelScope.async(Dispatchers.IO) {
                pogodynkaDao.getAllFavoriteCoords()
            }
            for(coord in favoriteCoordsList.await()){
                viewModelScope.launch(Dispatchers.IO) {
                    val weather = WeatherApi.api.getWeather(coord.lat, coord.lon).body()!!
                    weather.coord.name=coord.name
                    weather.favorite = true
                    _favoriteWeatherList.addItem(weather)
                }
            }
        }
    }

    fun isWeatherFavorite(locName : String) : Int{
        for(fav in favoriteWeatherList.value!!){
            if(fav.coord.name==locName){
                return favoriteWeatherList.value!!.indexOf(fav)
            }
        }
        return -1
    }

    fun updateFavoriteWeather(index : Int, weather : WeatherApiResponse){
        val oldValue = _favoriteWeatherList.value!!
        val favStatus = oldValue[index].favorite
        oldValue[index] = weather
        oldValue[index].favorite = favStatus
        _weatherResponse.value!!.body()!!.favorite = favStatus
        _favoriteWeatherList.value = oldValue
    }

    fun addFavoriteWeather(weather : WeatherApiResponse){
        _favoriteWeatherList.addItem(weather)
        viewModelScope.launch(Dispatchers.IO) {
            pogodynkaDao.insertFavoriteCoord(weather.coord)
        }
    }

    fun removeFavoriteWeather(weather : WeatherApiResponse){
        _favoriteWeatherList.removeItem(weather)
        viewModelScope.launch(Dispatchers.IO) {
            pogodynkaDao.deleteFavoriteCoord(weather.coord)
        }
    }

    fun clearWeatherResponse(){
        _weatherResponse.postValue(null)
    }

    private fun <T> MutableLiveData<MutableList<T>>.addItem(item: T) {
        val oldValue = this.value ?: mutableListOf()
        oldValue.add(item)
        this.postValue(oldValue)
    }

    private fun <T> MutableLiveData<MutableList<T>>.removeItem(item: T) {
        val oldValue = this.value ?: mutableListOf()
        oldValue.remove(item)
        this.postValue(oldValue)
    }
}

class WeatherViewModelFactory(private val pogodynkaDao: PogodynkaDao) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(pogodynkaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
