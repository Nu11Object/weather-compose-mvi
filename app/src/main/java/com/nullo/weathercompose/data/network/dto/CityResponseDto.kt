package com.nullo.weathercompose.data.network.dto

import com.google.gson.annotations.SerializedName

data class CityResponseDto (
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("country") val country: String,
)
