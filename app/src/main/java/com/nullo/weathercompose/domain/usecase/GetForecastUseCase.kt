package com.nullo.weathercompose.domain.usecase

import com.nullo.weathercompose.domain.repository.WeatherRepository
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(
    private val repository: WeatherRepository,
) {

    suspend operator fun invoke(cityId: Int) = repository.getForecast(cityId)
}
