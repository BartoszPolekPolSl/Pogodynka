package com.example.pogodynka.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pogodynka.data.model.WeatherApiResponse

@Database(
    entities = [WeatherApiResponse.Coord::class],
    version = 1,
    exportSchema = false
)
abstract class PogodynkaDatabase : RoomDatabase() {
    abstract fun pogodynkaDao(): PogodynkaDao

    companion object {
        @Volatile
        private var INSTANCE: PogodynkaDatabase? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = INSTANCE ?: synchronized(LOCK) {
            INSTANCE ?: buildDatabase(context).also {
                INSTANCE = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                PogodynkaDatabase::class.java,
                "pogodynka_database"
            ).build()

    }
}