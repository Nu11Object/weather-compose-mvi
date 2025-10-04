package com.nullo.weathercompose.data.mapper

import com.nullo.weathercompose.data.local.model.CityDbModel
import com.nullo.weathercompose.data.network.dto.CityResponseDto
import com.nullo.weathercompose.domain.entity.City

fun City.toDbModel(): CityDbModel = CityDbModel(id, name, country)

fun CityDbModel.toEntities(): City = City(id, name, country)

fun List<CityDbModel>.toEntities(): List<City> = map { it.toEntities() }
