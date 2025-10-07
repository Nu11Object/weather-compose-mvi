package com.nullo.weathercompose.domain.entity

import java.util.Calendar

data class Weather(
    val tempC: Float,
    val tempFeelsLikeC: Float,
    val windKph: Float,
    val windDirection: String,
    val pressureInch: Float,
    val humidity: Int,
    val uvIndex: Float,
    val precipitationMm: Float,
    val conditionText: String,
    val conditionIconUrl: String,
    val date: Calendar,
)
