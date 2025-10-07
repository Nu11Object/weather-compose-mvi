package com.nullo.weathercompose.data.mapper

import com.nullo.weathercompose.data.network.dto.CurrentWeatherDto
import com.nullo.weathercompose.data.network.dto.ForecastResponseDto
import com.nullo.weathercompose.data.network.dto.WeatherResponseDto
import com.nullo.weathercompose.domain.entity.Forecast
import com.nullo.weathercompose.domain.entity.UpcomingItem
import com.nullo.weathercompose.domain.entity.Weather
import java.util.Calendar
import java.util.Date

private const val ICON_SIZE_SMALL = "64x64"
private const val ICON_SIZE_LARGE = "128x128"

private fun Long.secondsToMillis(): Long = this * 1000

private fun Long.toCalendar() = Calendar.getInstance().apply {
    time = Date(this@toCalendar.secondsToMillis())
}

private fun String.withHttpsPrefix() = "https:$this"

private fun String.withLargeIcon() = replace(oldValue = ICON_SIZE_SMALL, newValue = ICON_SIZE_LARGE)

fun CurrentWeatherDto.toEntity(): Weather = Weather(
    tempC = tempC,
    tempFeelsLikeC = tempFeelsLikeC,
    windKph = windKph,
    windDirection = windDirection,
    pressureInch = pressureInch,
    humidity = humidity,
    uvIndex = uvIndex,
    precipitationMm = precipitationMm,
    conditionText = condition.text,
    conditionIconUrl = condition.iconUrl.withHttpsPrefix().withLargeIcon(),
    date = timestamp.toCalendar()
)

fun WeatherResponseDto.toEntity(): Weather = currentWeather.toEntity()

fun ForecastResponseDto.toEntity(): Forecast = Forecast(
    currentWeather = currentWeather.toEntity(),
    upcoming = forecast.days
        .drop(1) // Skip current day as it's already in currentWeather
        .map {
            UpcomingItem(
                minTempC = it.day.minTempC,
                maxTempC = it.day.maxTempC,
                conditionIconUrl = it.day.condition.iconUrl.withHttpsPrefix().withLargeIcon(),
                date = it.timestamp.toCalendar()
            )
        }
)
