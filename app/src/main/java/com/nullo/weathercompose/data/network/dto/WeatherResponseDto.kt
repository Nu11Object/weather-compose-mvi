package com.nullo.weathercompose.data.network.dto

import com.google.gson.annotations.SerializedName

data class WeatherResponseDto(
    @SerializedName("current") val currentWeather: CurrentWeatherDto
)

data class CurrentWeatherDto(
    @SerializedName("last_updated_epoch") val timestamp: Long,
    @SerializedName("temp_c") val tempC: Float,
    @SerializedName("feelslike_c") val tempFeelsLikeC: Float,
    @SerializedName("wind_kph") val windKph: Float,
    @SerializedName("wind_dir") val windDirection: String,
    @SerializedName("pressure_in") val pressureInch: Float,
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("uv") val uvIndex: Float,
    @SerializedName("precip_mm") val precipitationMm: Float,
    @SerializedName("condition") val condition: ConditionDto
)

data class ConditionDto(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val iconUrl: String,
)
