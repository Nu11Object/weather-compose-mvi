package com.nullo.weathercompose.domain.usecase

import com.nullo.weathercompose.domain.repository.FavouriteRepository
import javax.inject.Inject

class ObserveFavouriteStateUseCase @Inject constructor(
    private val repository: FavouriteRepository,
) {

    operator fun invoke(cityId: Int) = repository.observeIsFavourite(cityId)
}
