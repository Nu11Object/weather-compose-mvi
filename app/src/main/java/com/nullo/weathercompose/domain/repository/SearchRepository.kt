package com.nullo.weathercompose.domain.repository

import com.nullo.weathercompose.domain.entity.City

interface SearchRepository {

    suspend fun search(query: String): List<City>
}