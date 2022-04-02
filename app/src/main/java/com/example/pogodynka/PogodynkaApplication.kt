package com.example.pogodynka

import android.app.Application
import com.example.pogodynka.data.database.PogodynkaDatabase
import com.example.pogodynka.data.viewmodels.WeatherViewModelFactory
import com.example.pogodynka.network.NetworkConnectionInterceptor
import com.example.pogodynka.network.WeatherApiService
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class PogodynkaApplication :Application(), KodeinAware {
    override val kodein: Kodein = Kodein.lazy {
        import(androidXModule(this@PogodynkaApplication ))
        bind() from singleton { NetworkConnectionInterceptor(instance( )) }
        bind() from singleton {  WeatherApiService(instance( )) }
        bind() from singleton { PogodynkaDatabase(instance()) }
        bind() from provider { WeatherViewModelFactory(instance(),instance()) }
    }

}