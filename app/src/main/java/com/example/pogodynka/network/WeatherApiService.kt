package com.example.pogodynka.network

import com.example.pogodynka.BuildConfig
import com.example.pogodynka.data.model.WeatherApiResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(Interceptor { chain ->
        val request = chain.request().newBuilder()
        val originalHttpUrl = chain.request().url()
        val url = originalHttpUrl.newBuilder().addQueryParameter("appid", BuildConfig.WEATHER_API_KEY).build()
        request.url(url)
        return@Interceptor chain.proceed(request.build())
    }).build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .client(okHttpClient)
    .baseUrl(BASE_URL)
    .build()



interface WeatherApiService {
    @GET()
    suspend fun getWeather(
        @Query("lat") lat : Double,
        @Query("lon") lon : Double,
    ): Response<WeatherApiResponse>
}

object WeatherApi{
    val api : WeatherApiService by lazy { retrofit.create(WeatherApiService::class.java) }
}