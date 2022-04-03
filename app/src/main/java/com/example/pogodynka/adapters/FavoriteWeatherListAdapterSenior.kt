package com.example.pogodynka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.pogodynka.data.model.WeatherApiResponse
import com.example.pogodynka.databinding.WeatherCardViewBinding
import com.example.pogodynka.databinding.WeatherCardViewSBinding
import com.example.pogodynka.util.WeatherApiResponseHelper
import kotlin.math.roundToInt

//TODO change adapter to Diff
class FavoriteWeatherListAdapterSenior(
    private val favoriteWeatherList: LiveData<MutableList<WeatherApiResponse>>,
    private val onViewClicked: (WeatherApiResponse) -> Unit,
    private val onFavButtonClicked : (WeatherApiResponse) -> Unit
) :
    RecyclerView.Adapter<FavoriteWeatherListAdapterSenior.FavoriteWeatherListViewHolderSenior>() {

    inner class FavoriteWeatherListViewHolderSenior(private val binding: WeatherCardViewSBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(weatherApiResponse: WeatherApiResponse) {
            binding.txtViewTemp.text = weatherApiResponse.main.temp.roundToInt().toString()
            binding.txtViewLocation.text = weatherApiResponse.coord.name
            binding.imageViewWeather.setImageResource(
                WeatherApiResponseHelper.getWeatherIcon(
                    weatherApiResponse.weather[0].icon
                )
            )
            binding.textClock.timeZone =
                WeatherApiResponseHelper.convertToTimeZone(weatherApiResponse.timezone).id
            binding.txtViewDescription.text = weatherApiResponse.weather[0].description
            binding.buttonFav.setOnClickListener{ onFavButtonClicked(weatherApiResponse) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavoriteWeatherListViewHolderSenior {
        return FavoriteWeatherListViewHolderSenior(
            WeatherCardViewSBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteWeatherListViewHolderSenior, position: Int) {
        val current = favoriteWeatherList.value?.get(position)
        holder.bind(current!!)
        holder.itemView.setOnClickListener{ onViewClicked(current) }
    }

    override fun getItemCount(): Int {
        return favoriteWeatherList.value?.count() ?: 0
    }
}