package com.nullo.weathercompose.presentation.extensions

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun Calendar.formatToFullDate(): String {
    val format = SimpleDateFormat("d MMMM y", Locale.getDefault())
    return format.format(time)
}

fun Calendar.formatToFullWeek(): String {
    val format = SimpleDateFormat("EEEE", Locale.getDefault())
    return format.format(time)
}
