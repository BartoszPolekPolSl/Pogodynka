package com.example.pogodynka.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.pogodynka.data.model.WeatherApiResponse
import com.example.pogodynka.databinding.WeatherCardViewBinding
import com.example.pogodynka.other.WeatherApiResponseHelper
import kotlin.math.roundToInt

//TODO change adapter to Diff
class FavoriteWeatherListAdapter(
    private val favoriteWeatherList: LiveData<MutableList<WeatherApiResponse>>,
    private val onViewClicked: (WeatherApiResponse) -> Unit,
    private val onFavButtonClicked : (WeatherApiResponse) -> Unit
) :
    RecyclerView.Adapter<FavoriteWeatherListAdapter.FavoriteWeatherListViewHolder>() {

    inner class FavoriteWeatherListViewHolder(private val binding: WeatherCardViewBinding) :
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
    ): FavoriteWeatherListViewHolder {
        return FavoriteWeatherListViewHolder(
            WeatherCardViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FavoriteWeatherListViewHolder, position: Int) {
        val current = favoriteWeatherList.value?.get(position)
        holder.bind(current!!)
        holder.itemView.setOnClickListener{ onViewClicked(current) }
    }

    override fun getItemCount(): Int {
        return favoriteWeatherList.value?.count() ?: 0
    }
}