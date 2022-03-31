package com.example.pogodynka.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.pogodynka.data.model.WeatherApiResponse

@Dao
interface PogodynkaDao {
    @Insert
    suspend fun insertFavoriteCoord(favoriteCoord : WeatherApiResponse.Coord)


    @Delete
    suspend fun deleteFavoriteCoord(favoriteCoord : WeatherApiResponse.Coord)

    @Query("SELECT * FROM favorite_coord")
    suspend fun getAllFavoriteCoords() : List<WeatherApiResponse.Coord>
}