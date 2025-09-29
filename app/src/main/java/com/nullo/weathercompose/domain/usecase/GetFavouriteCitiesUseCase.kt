package com.nullo.weathercompose.domain.usecase

import com.nullo.weathercompose.domain.repository.FavouriteRepository
import javax.inject.Inject

class GetFavouriteCitiesUseCase @Inject constructor(
    private val repository: FavouriteRepository,
) {

    operator fun invoke() = repository.favouriteCities
}
