package com.example.pogodynka.data.viewmodels

import androidx.lifecycle.*
import com.example.pogodynka.data.database.PogodynkaDatabase
import com.example.pogodynka.data.model.WeatherApiResponse
import com.example.pogodynka.network.WeatherApiService
import com.example.pogodynka.other.NoInternetException
import kotlinx.coroutines.*
import retrofit2.Response


class WeatherViewModel(
    private val pogodynkaDb: PogodynkaDatabase,
    private val weatherApi: WeatherApiService
) : ViewModel() {

    var seniorMode : Boolean = false

    private var _failureMessage : MutableLiveData<String> = MutableLiveData()
    val failureMessage : LiveData<String>
        get() = _failureMessage

    private var _weatherResponse: MutableLiveData<Response<WeatherApiResponse>> = MutableLiveData()
    val weatherResponse: LiveData<Response<WeatherApiResponse>>
        get() = _weatherResponse

    private val _favoriteWeatherList: MutableLiveData<MutableList<WeatherApiResponse>> =
        MutableLiveData(
            mutableListOf()
        )
    val favoriteWeatherList: LiveData<MutableList<WeatherApiResponse>>
        get() = _favoriteWeatherList


    init {
        loadFavoriteWeather()
    }

    fun getWeather(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _weatherResponse.postValue(weatherApi.getWeather(lat, lon))
            } catch (e: NoInternetException) {
                _failureMessage.postValue(e.message)
            }

        }
    }


    fun loadFavoriteWeather(): Deferred<Unit> {
        return viewModelScope.async(Dispatchers.IO) {
            val favoriteCoordsList = viewModelScope.async(Dispatchers.IO) {
                pogodynkaDb.pogodynkaDao().getAllFavoriteCoords()
            }
            try{
                for (coord in favoriteCoordsList.await()) {
                    viewModelScope.async(Dispatchers.IO) {

                        val weather = weatherApi.getWeather(coord.lat, coord.lon)

                        if (weather.isSuccessful) {
                            weather.body()!!.coord.name = coord.name
                            weather.body()!!.favorite = true
                            _favoriteWeatherList.addItem(weather.body()!!)
                        }
                    }.await()
                }
            }
            catch (e:NoInternetException){
                _failureMessage.postValue(e.message)
            }
        }
    }

    fun getFavoriteWeatherIndex(weather: WeatherApiResponse): Int {
        for (fav in favoriteWeatherList.value!!) {
            if (fav.coord.name == weather.coord.name) {
                return favoriteWeatherList.value!!.indexOf(fav)
            }
        }
        return -1
    }

    fun updateFavoriteWeather(index: Int, weather: WeatherApiResponse) {
        val oldValue = _favoriteWeatherList.value!!
        val favStatus = oldValue[index].favorite
        oldValue[index] = weather
        oldValue[index].favorite = favStatus
        _favoriteWeatherList.value = oldValue
    }

    fun addFavoriteWeather(weather: WeatherApiResponse) {
        _favoriteWeatherList.addItem(weather)
        viewModelScope.launch(Dispatchers.IO) {
            pogodynkaDb.pogodynkaDao().insertFavoriteCoord(weather.coord)
        }
    }

    fun removeFavoriteWeather(weather: WeatherApiResponse) {
        _favoriteWeatherList.removeItem(weather)
        viewModelScope.launch(Dispatchers.IO) {
            pogodynkaDb.pogodynkaDao().deleteFavoriteCoord(weather.coord)
        }
    }

    fun clearWeatherResponse() {
        _weatherResponse.postValue(null)
        _failureMessage.postValue(null)
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

class WeatherViewModelFactory(
    private val pogodynkaDB: PogodynkaDatabase,
    private val weatherApi: WeatherApiService
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(pogodynkaDB, weatherApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
