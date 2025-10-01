package com.nullo.weathercompose.data.network.dto

import com.google.gson.annotations.SerializedName

data class ForecastResponseDto(
    @SerializedName("current") val currentWeather: CurrentWeatherDto,
    @SerializedName("forecast") val forecast: ForecastDto,
)

data class ForecastDto(
    @SerializedName("forecastday") val days: List<ForecastDayDto>,
)

data class ForecastDayDto(
    @SerializedName("date_epoch") val timestamp: Long,
    @SerializedName("day") val day: DayWeatherDto,
)

data class DayWeatherDto(
    @SerializedName("avgtemp_c") val tempC: Float,
    @SerializedName("condition") val condition: ConditionDto,
)
