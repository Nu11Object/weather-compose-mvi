package com.nullo.weathercompose.domain.repository

import com.nullo.weathercompose.domain.entity.Forecast
import com.nullo.weathercompose.domain.entity.Weather

interface WeatherRepository {

    suspend fun getWeather(cityId: Int): Weather

    suspend fun getForecast(cityId: Int): Forecast
}