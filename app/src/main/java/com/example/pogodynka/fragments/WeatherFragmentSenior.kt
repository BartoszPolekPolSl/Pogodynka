package com.example.pogodynka.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.pogodynka.R
import com.example.pogodynka.util.WeatherApiResponseHelper
import com.example.pogodynka.data.model.WeatherApiResponse
import com.example.pogodynka.data.viewmodels.WeatherViewModel
import com.example.pogodynka.data.viewmodels.WeatherViewModelFactory
import com.example.pogodynka.databinding.WeatherFragmentBinding
import com.example.pogodynka.databinding.WeatherFragmentSBinding
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.closestKodein
import org.kodein.di.generic.instance


import kotlin.math.roundToInt

class WeatherFragmentSenior : Fragment(), KodeinAware {

    override val kodein by closestKodein()
    private val factory: WeatherViewModelFactory by instance()
    private val navigationArgs: WeatherFragmentArgs by navArgs()
    private val viewModel: WeatherViewModel by activityViewModels { factory }

    private var _binding: WeatherFragmentSBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WeatherFragmentSBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadWeather(navigationArgs.weatherApiResponse)
        binding.buttonFav.setOnClickListener {
            onClickFavButton(navigationArgs.weatherApiResponse)
        }
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }

    private fun loadWeather(weather: WeatherApiResponse) {
        val favoriteIndex = viewModel.getFavoriteWeatherIndex(weather)
        if (favoriteIndex != -1) {
            viewModel.updateFavoriteWeather(favoriteIndex, weather)
        }
        bindWeather(weather)
    }

    private fun onClickFavButton(weather: WeatherApiResponse) {
        if (weather.favorite) {
            weather.favorite = false
            viewModel.removeFavoriteWeather(weather)
            binding.buttonFav.setImageResource(R.drawable.ic_baseline_star_outline_24)
        } else {
            weather.favorite = true
            viewModel.addFavoriteWeather(weather)
            binding.buttonFav.setImageResource(R.drawable.ic_baseline_star_24)
        }
    }


    private fun bindWeather(weatherApiResponse: WeatherApiResponse) {
        binding.txtViewLocation.text = weatherApiResponse.coord.name
        binding.txtViewDescription.text = weatherApiResponse.weather[0].description
        binding.txtViewHumidityValue.text = "${weatherApiResponse.main.humidity}%"
        binding.txtViewPressureValue.text = "${weatherApiResponse.main.pressure} mbar"
        binding.txtViewTemp.text = weatherApiResponse.main.temp.roundToInt().toString()
        binding.txtViewWindValue.text = "${weatherApiResponse.wind.speed} m/s"
        binding.txtViewCloudsValue.text = "${weatherApiResponse.clouds.all}%"
        binding.txtViewFeelsLike.text = "feels ${weatherApiResponse.main.feels_like.roundToInt()}"
        binding.txtViewDate.text = WeatherApiResponseHelper.getDate(weatherApiResponse.timezone)
        binding.textClock.timeZone =
            WeatherApiResponseHelper.convertToTimeZone(weatherApiResponse.timezone).id
        binding.imageViewWeatherIcon.setImageResource(
            WeatherApiResponseHelper.getWeatherIcon(
                weatherApiResponse.weather[0].icon
            )
        )
        binding.txtViewSunriseValue.text = WeatherApiResponseHelper.timeStampToDateTime(
            weatherApiResponse.sys.sunrise,
            weatherApiResponse.timezone
        )
        binding.txtViewSunsetValue.text = WeatherApiResponseHelper.timeStampToDateTime(
            weatherApiResponse.sys.sunset,
            weatherApiResponse.timezone
        )
        if (weatherApiResponse.favorite) {
            binding.buttonFav.setImageResource(R.drawable.ic_baseline_star_24)
        } else {
            binding.buttonFav.setImageResource(R.drawable.ic_baseline_star_outline_24)
        }
        //ToDo Poprawa wyświetlania godziny wschodu i zachodu słońca
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}