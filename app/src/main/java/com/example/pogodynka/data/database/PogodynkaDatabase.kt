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
abstract class PogodynkaDatabase : RoomDatabase(){
    abstract fun pogodynkaDao() : PogodynkaDao

    companion object {
        @Volatile
        private var INSTANCE: PogodynkaDatabase? = null

        fun getDatabase(context: Context): PogodynkaDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PogodynkaDatabase::class.java,
                    "pogodynka_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}