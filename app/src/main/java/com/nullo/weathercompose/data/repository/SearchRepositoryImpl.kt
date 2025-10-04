package com.nullo.weathercompose.data.repository

import com.nullo.weathercompose.data.mapper.toEntities
import com.nullo.weathercompose.data.network.api.ApiService
import com.nullo.weathercompose.domain.entity.City
import com.nullo.weathercompose.domain.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : SearchRepository {

    override suspend fun search(query: String): List<City> {
        return apiService.searchCity(query).toEntities()
    }
}
