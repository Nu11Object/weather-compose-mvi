package com.nullo.weathercompose.presentation.extensions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Compress
import androidx.compose.material.icons.outlined.DeviceThermostat
import androidx.compose.material.icons.outlined.Umbrella
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.nullo.weathercompose.R
import com.nullo.weathercompose.domain.entity.Weather
import com.nullo.weathercompose.presentation.details.WeatherDetail

fun Int.withPercent(): String = "$this%"

fun Float.inchToMm() = this * 25.4f

@Composable
fun Weather.toDetailsList(): List<WeatherDetail> = listOf(
    WeatherDetail(
        nameStringRes = R.string.feels_like,
        value = stringResource(R.string.template_temperature, tempFeelsLikeC),
        icon = Icons.Outlined.DeviceThermostat,
    ),
    WeatherDetail(
        nameStringRes = R.string.wind,
        value = stringResource(R.string.template_wind, windKph, windDirection),
        icon = Icons.Outlined.Air,
    ),
    WeatherDetail(
        nameStringRes = R.string.pressure,
        value = stringResource(R.string.template_pressure, pressureInch.inchToMm()),
        icon = Icons.Outlined.Compress,
    ),
    WeatherDetail(
        nameStringRes = R.string.humidity,
        value = humidity.withPercent(),
        icon = Icons.Outlined.WaterDrop,
    ),
    WeatherDetail(
        nameStringRes = R.string.precipitation,
        value = stringResource(R.string.template_precipitation, precipitationMm),
        icon = Icons.Outlined.Umbrella,
    ),
    WeatherDetail(
        nameStringRes = R.string.uv_index,
        value = uvIndex.toString(),
        icon = Icons.Outlined.WbSunny,
    )
)
