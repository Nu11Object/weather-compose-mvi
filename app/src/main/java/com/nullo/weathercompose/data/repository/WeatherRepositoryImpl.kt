package com.nullo.weathercompose.data.repository

import com.nullo.weathercompose.data.mapper.toEntity
import com.nullo.weathercompose.data.network.api.ApiService
import com.nullo.weathercompose.domain.entity.Forecast
import com.nullo.weathercompose.domain.entity.Weather
import com.nullo.weathercompose.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
): WeatherRepository {

    override suspend fun getWeather(cityId: Int): Weather {
        return apiService.getCurrentWeather("$PREFIX_CITY_ID$cityId").toEntity()
    }

    override suspend fun getForecast(cityId: Int): Forecast {
        return apiService.getForecast("$PREFIX_CITY_ID$cityId").toEntity()
    }

    companion object {

        private const val PREFIX_CITY_ID = "id:"
    }
}
