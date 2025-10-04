package com.nullo.weathercompose.data.mapper

import com.nullo.weathercompose.data.network.dto.CityResponseDto
import com.nullo.weathercompose.domain.entity.City

fun CityResponseDto.toEntity(): City = City(id, name, country)

fun List<CityResponseDto>.toEntities(): List<City> = map { it.toEntity() }
