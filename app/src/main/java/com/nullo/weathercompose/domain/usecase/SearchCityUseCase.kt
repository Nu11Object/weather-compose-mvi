package com.nullo.weathercompose.domain.usecase

import com.nullo.weathercompose.domain.repository.SearchRepository
import javax.inject.Inject

class SearchCityUseCase @Inject constructor(
    private val repository: SearchRepository,
) {

    suspend operator fun invoke(query: String) = repository.search(query)
}
