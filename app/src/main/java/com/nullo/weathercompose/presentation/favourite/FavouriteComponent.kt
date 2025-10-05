package com.nullo.weathercompose.presentation.favourite

import com.nullo.weathercompose.domain.entity.City
import kotlinx.coroutines.flow.StateFlow

interface FavouriteComponent {

    val model: StateFlow<FavouriteStore.State>

    fun onSearchClick()

    fun onAddFavouriteClick()

    fun onCityItemClick(city: City)
}
