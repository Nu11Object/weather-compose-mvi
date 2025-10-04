package com.nullo.weathercompose.data.mapper

import com.nullo.weathercompose.data.network.dto.CurrentWeatherDto
import com.nullo.weathercompose.data.network.dto.ForecastResponseDto
import com.nullo.weathercompose.data.network.dto.WeatherResponseDto
import com.nullo.weathercompose.domain.entity.Forecast
import com.nullo.weathercompose.domain.entity.Weather
import java.util.Calendar
import java.util.Date

private fun Long.secondsToMillis(): Long = this * 1000

private fun Long.toCalendar() = Calendar.getInstance().apply {
    time = Date(this@toCalendar.secondsToMillis())
}

private fun String.withHttpsPrefix() = "https:$this"

private fun String.withLargeIcon() = replace(oldValue = "64x64", newValue = "128x128")

fun CurrentWeatherDto.toEntity(): Weather = Weather(
    tempC = tempC,
    conditionText = condition.text,
    conditionIconUrl = condition.iconUrl.withHttpsPrefix().withLargeIcon(),
    date = timestamp.toCalendar()
)

fun WeatherResponseDto.toEntity(): Weather = currentWeather.toEntity()

fun ForecastResponseDto.toEntity(): Forecast = Forecast(
    currentWeather = currentWeather.toEntity(),
    upcoming = forecast.days.drop(1).map {
        Weather(
            tempC = it.day.tempC,
            conditionText = it.day.condition.text,
            conditionIconUrl = it.day.condition.iconUrl.withHttpsPrefix().withLargeIcon(),
            date = it.timestamp.toCalendar()
        )
    }
)
