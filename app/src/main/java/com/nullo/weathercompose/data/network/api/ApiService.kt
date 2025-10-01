package com.nullo.weathercompose.data.network.api

import com.nullo.weathercompose.data.network.dto.CityResponseDto
import com.nullo.weathercompose.data.network.dto.ForecastResponseDto
import com.nullo.weathercompose.data.network.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("current.json")
    suspend fun getCurrentWeather(
        @Query("q") query: String,
    ): WeatherResponseDto

    @GET("forecast.json")
    suspend fun getForecast(
        @Query("q") query: String,
        @Query("days") days: Int = DEFAULT_FORECAST_DAYS,
    ): ForecastResponseDto

    @GET("search.json")
    suspend fun searchCity(
        @Query("q") query: String,
    ) : List<CityResponseDto>

    companion object {

        private const val DEFAULT_FORECAST_DAYS = 4
    }
}
