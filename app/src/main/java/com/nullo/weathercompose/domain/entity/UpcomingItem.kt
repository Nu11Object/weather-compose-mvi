package com.nullo.weathercompose.domain.entity

import java.util.Calendar

data class UpcomingItem(
    val minTempC: Float,
    val maxTempC: Float,
    val conditionIconUrl: String,
    val date: Calendar,
)
