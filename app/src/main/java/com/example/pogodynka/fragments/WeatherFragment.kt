package com.example.pogodynka.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.pogodynka.R
import com.example.pogodynka.other.WeatherApiResponseHelper
import com.example.pogodynka.data.model.WeatherApiResponse
import com.example.pogodynka.data.viewmodels.WeatherViewModel
import com.example.pogodynka.databinding.WeatherFragmentBinding
import kotlin.math.roundToInt

class WeatherFragment : Fragment() {

    private val navigationArgs: WeatherFragmentArgs by navArgs()
    private val viewModel: WeatherViewModel by viewModels()

    private lateinit var pref: SharedPreferences

    private var _binding: WeatherFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = WeatherFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getPreferences(Context.MODE_PRIVATE )
        viewModel.getWeather(navigationArgs.lat, navigationArgs.lon)
        viewModel.weatherResponse.observe(viewLifecycleOwner) {
            bindWeather(it.body()!!)
        }
        binding.buttonFav.setOnClickListener{

        }
    }

    private fun onClickFavButton(weather : WeatherApiResponse){
        if(viewModel.addOrRemoveWeatherFavorites(weather)){
            binding.buttonFav.setImageResource(R.drawable.ic_baseline_star_24)
        }
        else{
            binding.buttonFav.setImageResource(R.drawable.ic_baseline_star_outline_24)
        }
    }

    private fun bindWeather(weatherApiResponse: WeatherApiResponse) {
        binding.txtViewLocation.text = WeatherApiResponseHelper.getLocationName(
            navigationArgs.lat,
            navigationArgs.lon,
            requireContext()
        )
        binding.txtViewDescription.text = weatherApiResponse.weather[0].description
        binding.txtViewHumidityValue.text = "${weatherApiResponse.main.humidity}%"
        binding.txtViewPressureValue.text = "${weatherApiResponse.main.pressure} mbar"
        binding.txtViewTemp.text = weatherApiResponse.main.temp.roundToInt().toString()
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
        //ToDo Poprawa wyświetlania godziny wschodu i zachodu słońca
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}