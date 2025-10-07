package com.nullo.weathercompose.presentation.details

import androidx.compose.ui.graphics.vector.ImageVector

data class WeatherDetail(
    val nameStringRes: Int,
    val value: String,
    val icon: ImageVector
)
